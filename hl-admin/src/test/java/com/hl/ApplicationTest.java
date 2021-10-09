package com.hl;


import com.hl.hos.pojo.Hos_info;
import com.hl.hos.service.AttachedService;
import com.hl.hos.service.MailService;
import com.hl.hos.utils.CodeGenerator;
import com.hl.hos.utils.MD5Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest
{

    @Autowired
    AttachedService AttachedService;
    @Test
    public void contextLoads()
    {
        int length = "69989970e72b46cf97d1d05e8af3ea56_".length();
        System.out.println(length);
    }

    //代码生成器
    @Test
    public void codeGenerator(){
        //CodeGenerator.codeGenerator("hos","t_template");
        System.out.println(AttachedService.getById(1));
    }

    @Autowired
    MailService mailService;
    @Test
    public void EmailTest(){
        mailService.sendSimpleMail(
                "yxhxsys@163.com",
                "126179411@qq.com",
                "yxhxsys@163.com",
                "测试邮件主题",
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>邮件提醒</title>\n" +
                        "    　　\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
                        "    <!--<script type=\"text/javascript\">-->\n" +
                        "        <!--//        var para = function () {-->\n" +
                        "        <!--//        }-->\n" +
                        "        <!--var url = document.location.toString();-->\n" +
                        "        <!--var arrUrl = url.split(\"?\");-->\n" +
                        "        <!--var para = arrUrl[1];-->\n" +
                        "        <!--alert('===='+url);-->\n" +
                        "    <!--</script>-->\n" +
                        "</head>\n" +
                        "\n" +
                        "<body style=\"margin: 0; padding: 0;\">\n" +
                        "\n" +
                        "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border-collapse: collapse;\">\n" +
                        "\n" +
                        "    　\n" +
                        "    <tr>\n" +
                        "        <td>\n" +
                        "            <div style=\"margin: 20px;text-align: center;margin-top: 50px\">\n" +
                        "                <img src=\"https://cxbres.oss-cn-beijing.aliyuncs.com/CTSP/mail/header/20180904115147.png\" border=\"0\" style=\"display:block;width: 100%;height: 100%\">\n" +
                        "            </div>\n" +
                        "        </td>\n" +
                        "    </tr>\n" +
                        "\n" +
                        "    <tr>\n" +
                        "        <td>\n" +
                        "            <div style=\"border: #36649d 1px dashed;margin: 30px;padding: 20px\">\n" +
                        "                <label style=\"font-size: 22px;color: #36649d;font-weight: bold\">恭喜您，注册成功！</label>\n" +
                        "                <p style=\"font-size: 16px\">亲爱的&nbsp;<label style=\"font-weight: bold\"> XXX先生/女士</label>&nbsp; 您好！欢迎加入xxx。\n" +
                        "                </p>\n" +
                        "                <p style=\"font-size: 16px\">您已于2018年10月1日充值成功，希望更好的为您服务！</p>\n" +
                        "            </div>\n" +
                        "        </td>\n" +
                        "    </tr>\n" +
                        "    　\n" +
                        "    <tr>\n" +
                        "        <td>\n" +
                        "            <div style=\"margin: 40px\">\n" +
                        "                <p style=\"font-size: 16px\">xxx管理团队</p>\n" +
                        "                <p style=\"color:red;font-size: 14px \">（这是一封自动发送的邮件，请勿回复。）</p>\n" +
                        "\n" +
                        "            </div>\n" +
                        "        </td>\n" +
                        "    </tr>\n" +
                        "\n" +
                        "    <!--<tr>-->\n" +
                        "        <!--<td>-->\n" +
                        "            <!--<div style=\"font-size:14px;margin-left: 30px;margin-right: 30px;padding: 20px\">-->\n" +
                        "                <!--<img src=\"tile-wide.png\" alt=\"\" style=\"width: 100px;height: 100px\"/>-->\n" +
                        "                <!--<label style=\"font-size: 12px;display:block;\"> 关注公众号了解更多</label>-->\n" +
                        "                <!--&lt;!&ndash;<p style=\"font-size: 12px\">&ndash;&gt;-->\n" +
                        "                    <!--&lt;!&ndash;如有任何问题，可以与我们联系，我们将尽快为你解答。<br/>&ndash;&gt;-->\n" +
                        "                    <!--&lt;!&ndash;电话：4008-622-333&ndash;&gt;-->\n" +
                        "                <!--&lt;!&ndash;</p>&ndash;&gt;-->\n" +
                        "            <!--</div>-->\n" +
                        "        <!--</td>-->\n" +
                        "    <!--</tr>-->\n" +
                        "\n" +
                        "    <tr>\n" +
                        "        <td>\n" +
                        "            <div align=\"right\" style=\"margin: 40px;border-top: solid 1px gray\" id=\"bottomTime\">\n" +
                        "                <p style=\"margin-right: 20px\">xxx科技有限公司</p>\n" +
                        "                <label style=\"margin-right: 20px\">2018年08月30日</label>\n" +
                        "            </div>\n" +
                        "        </td>\n" +
                        "    </tr>\n" +
                        "</table>\n" +
                        "</body>\n" +
                        "</html>\n" +
                        "————————————————"
        );
    }



}
