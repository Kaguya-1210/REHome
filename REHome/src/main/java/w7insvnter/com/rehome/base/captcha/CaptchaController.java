package w7insvnter.com.rehome.base.captcha;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

@RestController
public class CaptchaController {

    @GetMapping("/captcha")
    public byte[] captcha(HttpSession session, HttpServletResponse response) throws IOException {
        int width = 150;
        int height = 50;
        int length = 4;
        String chars = "ABCDEFGHJKMNPQRSTWXYZabcdefhjkmnprstwxyz2345678";

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        Random random = new Random();
        // 背景噪点
        for (int i = 0; i < 80; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g.drawOval(x, y, 1, 1);
        }
        // 干扰线
        for (int i = 0; i < 4; i++) {
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }

        // 文本
        StringBuilder sb = new StringBuilder();
        g.setFont(new Font("Arial", Font.BOLD, 32));
        for (int i = 0; i < length; i++) {
            char c = chars.charAt(random.nextInt(chars.length()));
            sb.append(c);
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            int x = 20 + i * 30;
            int y = 35 + random.nextInt(6) - 3;
            g.drawString(String.valueOf(c), x, y);
        }
        g.dispose();

        // 写入响应
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] bytes = baos.toByteArray();
        session.setAttribute("CAPTCHA_CODE", sb.toString());
        response.setContentType("image/png");
        return bytes;
    }
}
