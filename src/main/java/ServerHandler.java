
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static  int port ;
    private final String remoteHost;
    private volatile Channel outboundChannel;

    public ServerHandler (String remoteHost,int port) {
        this.port = port;
        this.remoteHost = remoteHost;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws InterruptedException {
        System.out.println("Server Handler Channel Active");

        final Channel inboundChannel = ctx.channel();
        Bootstrap b = new Bootstrap();

        b.group(inboundChannel.eventLoop())
                .channel(inboundChannel.getClass())
                .handler(new ClientHandler(inboundChannel));

        ChannelFuture channelFuture = b.connect(remoteHost, port);
        outboundChannel = channelFuture.channel();
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    System.out.println("channel active SUCCESS");
                    inboundChannel.read();
                } else {
                    System.out.println("channel active fail");
                    inboundChannel.close();
                }
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Server Handler Channel Read");

        if (outboundChannel.isActive()) {
            outboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        System.out.println("SUCCESS");
                    } else {
                        System.out.println("FAILED");
                        channelFuture.channel().close();
                    }
                }
            });
        } else {
            System.out.println("Channel Inactive");
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            System.out.println("Server Channel Inactive");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }


}