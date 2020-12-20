package com.odin.spider.thirdparty.tushare.openapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class TushareApi {

    @Value("${tushare.token}")
    private String tuToken="66cdab7a757dcb728f8833f732a928791d67b6f38e1cd8d94bb79a0d";

    @Autowired
    private RestTemplate restTemplate=new RestTemplate();

    final String api = "http://api.tushare.pro";

    /**
     * 格式化成tushare API所需格式
     *
     * @param code
     * @return
     */
    public static String formatCode(String code) {
        //5开头，沪市基金或权证 60开头上证
        if (code.matches("^60.*|^68.*|^5.*")) {
            return String.format("%s.SH", code);
        }
        //1开头的，是深市基金 00开头是深圳
        else if (code.matches("^1.*|^00.*|^300...")) {
            return String.format("%s.SZ", code);
        }
        return null;
    }

    /**
     * post 方式提交
     *
     * @param params
     * @return
     */
    String post(JSONObject params) {
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型，这里用json所以是MediaType.APPLICATION_JSON
        headers.setContentType(MediaType.APPLICATION_JSON);
        params.put("token", tuToken);
        HttpEntity<String> formEntity = new HttpEntity<String>(params.toString(), headers);
        String result = restTemplate.postForObject(api, formEntity, String.class);
        return result;
    }


    /**
     * 返回已上市的A股代码
     *
     * @return TS代码, 股票代码, 股票名称, 所在地域, 所属行业, 股票全称, 市场类型 （主板/中小板/创业板）,上市状态： L上市 D退市 P暂停上市,上市日期
     */
    public JSONArray getStockCodeList() {
        JSONObject json = new JSONObject();
        //接口名称
        json.put("api_name", "stock_basic");
        //只取上市的
        json.put("params", JSON.parse("{'list_status':'L'}"));
        json.put("fields", "ts_code,symbol,name,area,industry,fullname,market,list_status,list_date");
        String result = post(json);
        JSONObject datas = JSON.parseObject(result);
        JSONArray items = datas.getJSONObject("data").getJSONArray("items");
        return items;
    }

    /**
     * 获取上海公司基础信息
     *
     * @return
     */
    public JSONArray getStockShCompany() {
        JSONObject json = new JSONObject();
        //接口名称
        json.put("api_name", "stock_company");
        json.put("params", JSON.parse("{'exchange':'SSE'}"));
        json.put("fields", "ts_code,chairman,manager,secretary,reg_capital,setup_date,province,city,introduction,website,email,office,employees,main_business,business_scope");
        String result = post(json);
        JSONObject data = JSON.parseObject(result);
        JSONArray items = data.getJSONObject("data").getJSONArray("items");
        return items;
    }

    /**
     * 获取深圳公司基础信息
     *
     * @return
     */
    public JSONArray getStockSZCompany() {
        JSONObject json = new JSONObject();
        //接口名称
        json.put("api_name", "stock_company");
        json.put("params", JSON.parse("{'exchange':'SZSE'}"));
        json.put("fields", "ts_code,chairman,manager,secretary,reg_capital,setup_date,province,city,introduction,website,email,office,employees,main_business,business_scope");
        String result = post(json);
        JSONObject data = JSON.parseObject(result);
        JSONArray items = data.getJSONObject("data").getJSONArray("items");
        return items;
    }

    /**
     * 得到前10大持有人
     *
     * @param code
     * @return
     */
    public JSONArray getStockTopHolders(String code) {
        JSONObject json = new JSONObject();
        //接口名称
        json.put("api_name", "top10_holders");
        json.put("params", JSON.parse(String.format("{'ts_code':'%s'}", code)));
        json.put("fields", "ts_code,ann_date,end_date,holder_name,hold_amount,hold_ratio");
        String result = post(json);
        JSONObject datas = JSON.parseObject(result);
        JSONArray items = datas.getJSONObject("data").getJSONArray("items");
        return items;
    }

    /**
     * 股东人数
     *
     * @param code       代码
     * @param start_date 开始日期
     * @param end_date   结束日期
     * @return
     */
    public JSONArray getStockHolderNumber(String code, String start_date, String end_date) {
        JSONObject json = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("ts_code", code);
        params.put("start_date", start_date);
        params.put("end_date", end_date);
        //接口名称
        json.put("api_name", "stk_holdernumber");
        json.put("params", params);
        String result = post(json);
        JSONObject datas = JSON.parseObject(result);
        JSONArray items = datas.getJSONObject("data").getJSONArray("items");
        return items;
    }

    /**
     * 日线行情
     *
     * @param code       代码
     * @param start_date 开始日期
     * @param end_date   结束日期
     * @return
     */
    public JSONArray getStockDailyPrice(String code, String start_date, String end_date) {
        JSONObject json = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("ts_code", code);
        params.put("start_date", start_date);
        params.put("end_date", end_date);
        //接口名称
        json.put("api_name", "daily");
        json.put("params", params);
        String result = post(json);
        JSONObject datas = JSON.parseObject(result);
        JSONArray items = datas.getJSONObject("data").getJSONArray("items");
        return items;
    }

    /**
     * 每日指标基本面
     *
     * @param code       代码
     * @param start_date 开始日期
     * @param end_date   结束日期
     * @return
     */
    public JSONArray getStockDailyBasic(String code, String start_date, String end_date) {
        JSONObject json = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("ts_code", code);
        params.put("start_date", start_date);
        params.put("end_date", end_date);
        //接口名称
        json.put("api_name", "daily_basic");
        json.put("params", params);
        String result = post(json);
        JSONObject datas = JSON.parseObject(result);
        JSONArray items = datas.getJSONObject("data").getJSONArray("items");
        return items;
    }

    /**
     * https://tushare.pro/document/2?doc_id=128
     * @param date
     * @return
     */
    public JSONArray index_dailybasic(String date){
        JSONObject json = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("trade_date", date);
        //接口名称
        json.put("api_name", "index_dailybasic");
        json.put("params", params);
        json.put("fields","ts_code,trade_date,turnover_rate,turnover_rate_f,pe,pe_ttm,pb");
        String result = post(json);
        JSONObject datas = JSON.parseObject(result);
        JSONArray items = datas.getJSONObject("data").getJSONArray("items");
        return items;
    }

    public static void main(String[] args) {
        JSONArray data = new TushareApi().index_dailybasic("20201218");
        for (int i = 0; i < data.size(); i++) {
            JSONArray ja = data.getJSONArray(i);
            System.out.println(ja);
        }
    }
}
