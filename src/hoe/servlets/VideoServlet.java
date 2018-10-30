package hoe.servlets;

import hoe.servers.GameServer;
import hoe.User;
import hoe.servers.AbstractServer;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;

public class VideoServlet extends HttpServletWithUserValidator {

    private static final int BUFFER_LENGTH = 1024 * 16;
    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;
    private static final Pattern RANGE_PATTERN = Pattern.compile("bytes=(?<start>\\d*)-(?<end>\\d*)");
    private final String videoPath = "./assets/videos/";

    public VideoServlet(AbstractServer server) {
        super(server);
    }

    @Override
    public void validateUser(HttpServletRequest request, HttpServletResponse response, User user, int requestType) throws IOException {
        /*
        if (null == user) {
            response.setStatus(HttpStatus.UNAUTHORIZED_401);
            return;
        }
         */
        int coords[] = {0, 0};
        int turn = 0;

        try {
            if (request.getPathInfo() != null) {
                String saTileCoords[] = request.getPathInfo().split("/");
                switch (saTileCoords.length) {
                    case 2:
                        coords[0] = Integer.parseInt(saTileCoords[1]);
                        break;
                    case 3:
                        coords[0] = Integer.parseInt(saTileCoords[1]);
                        coords[1] = Integer.parseInt(saTileCoords[2]);
                        break;
                    case 4:
                        coords[0] = Integer.parseInt(saTileCoords[1]);
                        coords[1] = Integer.parseInt(saTileCoords[2]);
                        turn = Integer.parseInt(saTileCoords[3]);
                        break;
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpStatus.BAD_REQUEST_400);
            return;
        }

        String videoFilename = "video_" + coords[0] + "_" + coords[1] + "_" + turn + ".mp4";
        Path video = Paths.get(videoPath, videoFilename);

        if (!Files.exists(video, (LinkOption[]) null)) {
            return;
        }

        int length = (int) Files.size(video);
        int start = 0;
        int end = length - 1;

        String range = request.getHeader("Range");
        Matcher matcher = RANGE_PATTERN.matcher(range);

        if (matcher.matches()) {
            String startGroup = matcher.group("start");
            start = startGroup.isEmpty() ? start : Integer.valueOf(startGroup);
            start = start < 0 ? 0 : start;

            String endGroup = matcher.group("end");
            end = endGroup.isEmpty() ? end : Integer.valueOf(endGroup);
            end = end > length - 1 ? length - 1 : end;
        }

        int contentLength = end - start + 1;

        response.reset();
        response.setBufferSize(BUFFER_LENGTH);
        response.setHeader("Content-Disposition", String.format("inline;filename=\"%s\"", videoFilename));
        response.setHeader("Accept-Ranges", "bytes");
        response.setDateHeader("Last-Modified", Files.getLastModifiedTime(video).toMillis());
        response.setDateHeader("Expires", System.currentTimeMillis() + EXPIRE_TIME);
        response.setContentType(Files.probeContentType(video));
        response.setHeader("Content-Range", String.format("bytes %s-%s/%s", start, end, length));
        response.setHeader("Content-Length", String.format("%s", contentLength));
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

        int bytesRead;
        int bytesLeft = contentLength;
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_LENGTH);

        try (SeekableByteChannel input = Files.newByteChannel(video);
                OutputStream output = response.getOutputStream()) {

            input.position(start);

            while ((bytesRead = input.read(buffer)) != -1 && bytesLeft > 0) {
                buffer.clear();
                output.write(buffer.array(), 0, bytesLeft < bytesRead ? bytesLeft : bytesRead);
                bytesLeft -= bytesRead;
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doUserValidation(request, response, GameServer.GET_REQUEST);
    }

    @Override
    protected String getDefaultPagePath() {
        return "";
    }

}
