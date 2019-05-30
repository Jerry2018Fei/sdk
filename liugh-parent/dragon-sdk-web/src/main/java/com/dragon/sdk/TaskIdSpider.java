package com.dragon.sdk;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * 使用Jsoup模拟登陆RASA并获取任务id
 *
 * @author dingpengfei
 * @date 2019-03-18 14:34
 */
@Component
@Data
public class TaskIdSpider {
  private static final Logger logger = LoggerFactory.getLogger(TaskIdSpider.class);

  @SuppressWarnings("deprecated")
  private static Response mockLogin(String userName, String pwd, String ua, String loginUrl)
      throws Exception {
    Connection con = Jsoup.connect(loginUrl);
    // 获取连接
    con.header("User-Agent", ua);
    con.validateTLSCertificates(false);
    Response rs = con.execute();
    logger.info(rs.body());
    // 获取响应
    Document d1 = Jsoup.parse(rs.body());
    // 转换为Dom树
    List<Element> et = d1.select(".lg-form");
    // 获取form表单，可以通过查看页面源码代码得知
    if (CollectionUtils.isEmpty(et)) {
      throw new Exception("获取登录表单信息失败");
    }
    // 获取，cooking和表单属性，下面map存放post时的数据
    Map<String, String> datas = new HashMap<>(10);
    for (Element e : et.get(0).getAllElements()) {
      if ("username".equals(e.attr("name"))) {
        e.attr("value", userName);
        // 设置用户名
      }
      if ("password".equals(e.attr("name"))) {
        e.attr("value", pwd);
        // 设置用户密码
      }
      // 排除空值表单属性
      if (e.attr("name").length() > 0) {
        datas.put(e.attr("name"), e.attr("value"));
      }
    }
    /*
     * 第二次请求，post表单数据，以及cookie信息
     *
     * <p>*
     */
    Connection con2 = Jsoup.connect("https://meng.youximao.com/web/session/loginByPwd");
    con2.header("User-Agent", ua);
    con2.validateTLSCertificates(false).timeout(900000);
    // 设置cookie和post上面的map数据
    con2.ignoreContentType(true)
        .referrer(loginUrl)
        .method(Method.POST)
        .data(datas)
        .cookies(rs.cookies());
    Response login = con2.execute();

    // 登陆成功后的cookie信息，可以保存到本地，以后登陆时，只需一次登陆即可

    Map<String, String> map = login.cookies();

    for (String s : map.keySet()) {
      logger.info("cookie:{},{}", s, map.get(s));
    }
    return login;
  }

  private static Map<String, String> handlerTaskConnectCommon(
      String ua, Response login, Connection con3) {
    con3.header("User-Agent", ua);
    con3.header("X-Requested-With", "XMLHttpRequest");
    con3.header("X-CSRFToken", login.cookie("csrftoken"));
    con3.validateTLSCertificates(false).timeout(100000);
    Map<String, String> params = new HashMap<>(10);
    params.put("csrfmiddlewaretoken", login.cookie("csrftoken"));
    return params;
  }

  public static void main(String[] args) throws Exception {

    String ua =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36";
    //    System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
   Response rep= mockLogin("qq1718502269", "1qa2ws3ed", ua, "https://m.youximao.com/#/login");
   logger.info(rep.body());
  }

  /**
   * * 爬虫rsas任务ID列表
   *
   * @author dingpengfei
   * @date 2019-03-18 15:11
   * @return void
   */
  //  public TreeSet<String> getRsasTaskIds(CoreUtils coreUtils) throws Exception {
  //    String startTime = coreUtils.getRunningParams().getLastRCollectTime();
  //    if (StringUtil.isEmpty(startTime)) {
  //      startTime =
  //          new SimpleDateFormat("yyyy-MM-dd")
  //              .format(
  //                  DateUtils.addDays(new Date(),
  // -coreUtils.getCoreParams().getFirstSpiderDays()));
  //    }
  //    TreeSet<String> ids = new TreeSet<>();
  //    try {
  //      // 第一次请求
  //      String loginUrl = httpsConstant.getSpiderConstant().getRSpiderFirstUrl();
  //      logger.info(loginUrl);
  //      Response login =
  //          mockLogin(
  //              coreUtils.getRTaskMessage().getUsername(),
  //              coreUtils.getRTaskMessage().getPassword(),
  //              httpsConstant.getSpiderConstant().getUserAgent(),
  //              loginUrl);
  //      Connection con3 = Jsoup.connect(httpsConstant.getSpiderConstant().getRSpiderSecondUrl());
  //      logger.info(httpsConstant.getSpiderConstant().getRSpiderSecondUrl());
  //      int pageNum = 1;
  //      int pageSize=100;
  //      readRIds(startTime, ids, login, con3, pageNum, pageSize);
  //      ids.comparator();
  //      logger.info("爬取rsas任务-任务结果：{}", JSONObject.toJSONString(ids));
  //      logger.info("爬取rsas任务-任务结果 size：{}", ids.size());
  //      //      logger.info(taskList.body());
  //      return CollectionUils.isEmpty(ids) ? new TreeSet<>() : ids;
  //    } catch (IOException e) {
  //      e.printStackTrace();
  //      logger.error(e.getLocalizedMessage());
  //
  //      throw new Exception(String.format("获取rsas任务列表异常:%s", e.getMessage()));
  //    }
  //  }

  private void readRIds(
      String startTime,
      TreeSet<String> ids,
      Response login,
      Connection con3,
      int pageNum,
      int pageSize)
      throws IOException {
    Map<String, String> params = handlerTaskConnectCommon("", login, con3);
    params.put("ip", "");
    params.put("task_name", "");
    params.put("domain", "");
    params.put("task_status", "");
    params.put("rs_template", "");
    params.put("tpl", "");
    params.put("account", "");
    params.put("time_start_scan", startTime);
    params.put("time_end_scan", "");
    params.put("task_type", "");
    params.put("task_id", "");

    params.put("page", String.valueOf(pageNum));
    params.put("page_count", String.valueOf(pageSize));
    params.put("bvs_template", "");
    params.put("protect_level", "");
    logger.info("爬取rsas任务-请求参数：{}", JSONObject.toJSONString(params));
    // 设置cookie和post上面的map数据
    con3.ignoreContentType(true)
        .referrer("")
        .method(Method.POST)
        .data(params)
        .cookies(login.cookies());
    Response taskList = con3.execute();
    Document d2 = Jsoup.parse(taskList.body());
    //    logger.info(d2.outerHtml());
    String msg0 = "</select>/页， 共510条 </span>";
    String msg =
        d2.outerHtml()
            .substring(
                d2.outerHtml().lastIndexOf("</select>/页， 共") + "</select>/页， 共".length(),
                d2.outerHtml().lastIndexOf("条 </span>"));
    logger.info("总数：{}", msg);
    Integer pageCount = 1;
    if (Integer.valueOf(msg) % pageSize == 0) {
      pageCount = Integer.valueOf(msg) / pageSize;
    } else {
      pageCount = Integer.valueOf(msg) / pageSize + 1;
    }
    // 转换为Dom树
    Elements checkbox = d2.select("input[type=checkbox][name=taskcheck]");

    for (Element e : checkbox) {
      ids.add(e.val());
    }
    if (pageCount > pageNum) {
      pageNum++;
      readRIds(startTime, ids, login, con3, pageNum, pageSize);
    }
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
