package com.freedom.health4j.api.impl.common;

import com.freedom.health4j.Constants;
import com.freedom.health4j.api.ReportNotifier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * email notifier.
 */
public class EmailNotifier implements ReportNotifier {

    private static final Log logger = LogFactory.getLog(EmailNotifier.class);

    private Properties commonConfig;

    public EmailNotifier(Properties commonConfig) {
        this.commonConfig = commonConfig;
    }

    @Override
    public void doNotify() {
        try {
            HtmlEmail email = new HtmlEmail();

            email.setHostName(commonConfig.getProperty("health4j.email.host"));
            email.setSmtpPort(Integer.valueOf(commonConfig.getProperty("health4j.email.port")));

            String authName = commonConfig.getProperty("health4j.email.auth.name");
            String authPwd = commonConfig.getProperty("health4j.email.auth.password");
            email.setAuthentication(authName, authPwd);

            email.setCharset("UTF-8");
            String subject = commonConfig.getProperty("health4j.email.subject");
            email.setSubject(commonConfig.getProperty(Constants.COMMON_PROJECT_KEY) + "-" + subject);

            email.addTo(commonConfig.getProperty("health4j.email.to"));
            email.setFrom(commonConfig.getProperty("health4j.email.from"));

            String cc = commonConfig.getProperty("health4j.email.cc");
            if (cc != null && cc.length() > 0)
                email.addCc(cc);

            String reportBasePathStr = commonConfig.getProperty(Constants.COMMON_REPORT_BASE_PATH_KEY);
            String reportPathStr = commonConfig.getProperty(Constants.COMMON_REPORT_PATH_KEY);
            Path reportPath = Paths.get(reportBasePathStr, reportPathStr);

            String htmlContent = new String(Files.readAllBytes(reportPath));
            email.setHtmlMsg(htmlContent);
            email.setTextMsg("你的邮箱当前不支持接收HTML格式的消息，请通过附件查看!");

            email.setDebug(logger.isDebugEnabled());
            email.buildMimeMessage();

            email.sendMimeMessage();
        } catch (EmailException | IOException e) {
            throw new RuntimeException(e.toString());
        }

    }


}
