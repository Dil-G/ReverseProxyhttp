
import io.netty.channel.*;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private final Channel inboundChannel;

    public ClientHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client Handler Channel Active");
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        System.out.println("Client Handler Channel Read");

        inboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    System.out.println("Client SUCCESS");
                    ctx.channel().read();
                } else {
                    System.out.println("Client fail");
                    future.channel().close();
                }
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (!inboundChannel.isActive()) {
            System.out.println("Client Channel Inactive");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}