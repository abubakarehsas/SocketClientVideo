package NetworkUtility;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
 
public class NIOSocketUtil extends Thread {
 
    private Selector selector = null;
    private SocketChannel client = null;
    private static int CONNECT_TIMEOUT = 10000;
    private static int READ_TIMEOUT = 10000;
    private static int RECONNECT_TIME = 10000;
 
    private final byte CONNECT = 1;
    private final byte RUNNING = 2;
    private byte STATE = CONNECT;
    private boolean onWork;// 是否工作状态
    static {
        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
 
    };
    public static int STATUS_OK = 1000;
    public static int STATUS_FAIL = 1001;
    private String ip = "127.0.0.1";
    private int port = 9527;
    private ConnectListener connectListener;
    private DataCallbackListener dataCallbackListener;
    private static final int BLOCK = 102400 * 100;
    private ByteBuffer readBuffer = ByteBuffer.allocate(BLOCK);// 100kb缓冲区
 
    public NIOSocketUtil(String ip, int port) {
        this.ip = ip;
        this.port = port;
        onWork = true;
    }
 
    public boolean isReady() {
        return STATE == RUNNING;
    }
 
    public void stopWork() {
        onWork = false;
        closeKey(null);
    }
 
    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (onWork) {
            switch (STATE) {
            case CONNECT:
                connect();
                break;
            case RUNNING:
                running();
                break;
            }
        }
    }
 
    private void running() {
        SelectionKey key = null;
        try {
            while (selector.select() > 0) {
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    read(key);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            closeKey(key);
        }
    }
 
    private final void read(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isReadable()) {
            SocketChannel client = (SocketChannel) selectionKey.channel();
            // 如果缓冲区过小的话那么信息流会分成多次接收
            int actual = client.read(readBuffer);
            if (actual > 0) {
                readBuffer.flip();
                int limit = readBuffer.limit();
                byte[] data = new byte[limit];
                readBuffer.get(data);
                readBuffer.clear();// 清空
                // process data
                if (dataCallbackListener != null)
                    dataCallbackListener.callback(data);
            }
        }
    }
 
    public final boolean write(byte[] data) {
        try {
            if (STATE == RUNNING && client != null && client.isConnected()) {
                ByteBuffer buffer = ByteBuffer.wrap(data);
                int size = buffer.limit();
                // 此处需加中途断开逻辑，下次再继续发送数据包
                int actually = client.write(buffer);
                if (actually == size)
                    return true;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            closeKey(null);
        }
        return false;
    }
 
    private void connect() {
        try {
            selector = Selector.open();
            InetSocketAddress isa = new InetSocketAddress(ip, port);
            client = SocketChannel.open();
            // 设置连超时
            client.socket().connect(isa, CONNECT_TIMEOUT);
            // 设置读超时
            client.socket().setSoTimeout(READ_TIMEOUT);
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            if (client.isConnected()) {
                // 连接成功开始监听服务端消息
                // 发送一个验证数据包到服务器进行验证
                STATE = RUNNING;
                if (connectListener != null)
                    connectListener.connect(STATUS_OK);
            } else {
                // 关闭通道过60S重新开始连接
                if (connectListener != null)
                    connectListener.connect(STATUS_FAIL);
                StringBuffer buffer = new StringBuffer("服务器连接失败");
                buffer.append(RECONNECT_TIME / 1000);
                buffer.append("秒后再尝试连接");
                if (connectListener != null)
                    connectListener.error(buffer.toString());
                closeKey(null);// 关闭通道
                Wait(RECONNECT_TIME);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // 有异常关闭通道过60S重新开始连接
            e.printStackTrace();
            StringBuffer buffer = new StringBuffer("连接出错");
            buffer.append(RECONNECT_TIME / 1000);
            buffer.append("秒后再尝试连接");
            if (connectListener != null)
                connectListener.error(buffer.toString());
            closeKey(null);// 关闭通道
            Wait(RECONNECT_TIME);
        }
    }
 
    private void closeKey(SelectionKey key) {
        STATE = CONNECT;
        try {
            if (client != null) {
                client.socket().close();
                client.close();
                client = null;
            }
            if (selector != null) {
                selector.close();
                selector = null;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (key != null) {
            key.cancel();
            try {
                key.channel().close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                StringBuffer buffer = new StringBuffer("连接断开");
                buffer.append(RECONNECT_TIME / 1000);
                buffer.append("秒后再尝试连接");
                if (connectListener != null)
                    connectListener.error(buffer.toString());
                Wait(RECONNECT_TIME);
            }
        }
    }
 
    private synchronized void Wait(long millis) {
        try {
            wait(millis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
 
    public void setDataCallbackListener(
            DataCallbackListener dataCallbackListener) {
        this.dataCallbackListener = dataCallbackListener;
    }
 
    public void setConnectListener(ConnectListener connectListener) {
        this.connectListener = connectListener;
    }
 
    public interface ConnectListener {
 
        public void connect(int status);
 
        public void error(String msg);
    }
 
    public interface DataCallbackListener {
 
        public void callback(byte[] data);
 
    }
 
}