package w7insvnter.com.rehome.base.captcha;

import cn.dustlight.captcha.annotations.CodeValue;
import cn.dustlight.captcha.annotations.SendCode;
import cn.dustlight.captcha.annotations.VerifyCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CaptchaController {

    private final Log log = LogFactory.getLog(CaptchaController.class.getSimpleName());

    /**
     * 获取验证码。
     * <p>
     * 方法的注解 '@SendCode' 表示此方法执行之前将生成验证码并进行发送。
     *
     * @param code 生成的验证码，通过注解 '@CodeValue' 传入
     */
    @RequestMapping("/captcha")
    @SendCode
    public void captcha(@CodeValue String code) {
        // 在此处进行自定义的业务，验证码的生成、发送与储存已由注解 '@SendCode' 完成。
        log.info(code);
    }

    /**
     * 消费验证码
     * <p>
     * 方法的注解 '@VerifyCode' 表示此方法执行之前先进行验证码验证。
     *
     * @param code 参数中的验证码，通过注解 '@CodeValue' 表示此参数为需要被验证的参数。
     * @return
     */
    @RequestMapping("/")
    @VerifyCode
    public String index(@CodeValue String code) {
        // 在此处进行自定义的业务，验证码的验证以及销毁已由注解 '@VerifyCode' 完成。
        return String.format("Hello World! (%s)", code);
    }
}
