package com.xlab.service.imp;

import com.google.gson.*;
import com.xlab.config.RestTemplateConfig;
import com.xlab.entity.Request;
import com.xlab.entity.RequestBuilder;
import com.xlab.entity.Search;
import com.xlab.entity.SearchBuilder;
import com.xlab.entity.bean.WebZabbixRequest;
import com.xlab.service.ZabbixInfluxService;
import com.xlab.service.ZabbixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.Null;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ZabbixServiceImp implements ZabbixService {

    @Autowired
    private RestTemplateConfig restTemplateConfig;

    @Autowired
    private ZabbixInfluxService zabbixInfluxService;

    private WebZabbixRequest webZabbixRequest;

    @Value("${pro.measurement}")
    private String proMeasurement;
    @Value("${inode.measurement}")
    private String inodeMeasurement;
    @Value("${disk.measurement}")
    private String diskMeasurement;
    @Value("${memory.measurement}")
    private String memoryMeasurement;
    @Value("${net.measurement}")
    private String netMeasurement;
    @Value("${cpu.measurement}")
    private String cpuMeasurement;

    private String auth;

    @Override
    public String apiVersion() {
        Request request = RequestBuilder.newBuilder().method("apiinfo.version").build();
        JsonObject response = call(request);
        return response.get("result").getAsString();
    }

    @Override
    public boolean setZabbixRequestSource(WebZabbixRequest webZabbixRequest) {
        this.webZabbixRequest = webZabbixRequest;
        return true;
    }

    @Override
    public boolean login() {
        this.auth = null;
        Request request = RequestBuilder.newBuilder().paramEntry("user", webZabbixRequest.getZabbixUsername()).paramEntry("password", webZabbixRequest.getZabbixPassword()).method("user.login").build();
        JsonObject response = call(request);
        String auth = response.get("result").getAsString();
        if (auth != null && !auth.isEmpty()) {
            this.auth = auth;
            return true;
        }
        return false;
    }

    @Override
    public JsonObject call(Request request) {
        if (request.getAuth() == null) {
            request.setAuth(this.auth);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        Gson gson = new Gson();
        RestTemplate restTemplate = restTemplateConfig.restTemplate(restTemplateConfig.httpClientFactory());

        MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
        httpHeaders.setContentType(type);

        String requestJson = gson.toJson(request);
        HttpEntity<String> entity = new HttpEntity<>(requestJson, httpHeaders);
        String result = restTemplate.postForObject(webZabbixRequest.getZabbixUrl(), entity, String.class);

        JsonObject resultJson = new JsonParser().parse(result).getAsJsonObject();
        return resultJson;
    }

    @Override
    public void setDiscoveryDelay(String delay,String itemprototype) {

        Search searchKey_host = SearchBuilder.newBuilder().paramEntry("key_", webZabbixRequest.getZabbixHostname()).build();
        Request request_hostget=RequestBuilder.newBuilder()
                .paramEntry("search",searchKey_host.getParams())
                .method("host.get")
                .build();
        String hostid=call(request_hostget).get("result").getAsJsonArray().get(0).getAsJsonObject().get("hostid").getAsString();
        Search searchKey_discovery = SearchBuilder.newBuilder().paramEntry("key_", itemprototype).build();
        Request request_get = RequestBuilder.newBuilder()
                .paramEntry("hostids",hostid)
                .paramEntry("search", searchKey_discovery.getParams())
                .paramEntry("searchWildcardsEnabled",true)
                .method("discoveryrule.get")
                .build();
        JsonArray result=call(request_get).get("result").getAsJsonArray();
        Iterator discovery_it=result.iterator();
        while(discovery_it.hasNext()) {
            JsonObject discovery_e=(JsonObject) discovery_it.next();
            Request request_update = RequestBuilder.newBuilder()
                    .paramEntry("itemid", discovery_e.get("itemid").getAsString())
                    .paramEntry("delay", delay)
                    .method("discoveryrule.update")
                    .build();
            JsonObject updateObj = call(request_update);
            Request request_getpro = RequestBuilder.newBuilder()
                    .paramEntry("discoveryids", discovery_e.get("itemid").getAsString())
                    .method("itemprototype.get")
                    .build();
            JsonArray result_prototype = call(request_getpro).get("result").getAsJsonArray();
            Iterator iterator = result_prototype.iterator();
            while (iterator.hasNext()) {
                JsonObject element = (JsonObject) iterator.next();
                Request request_delay = RequestBuilder.newBuilder()
                        .paramEntry("itemid", element.get("itemid").getAsString())
                        .paramEntry("delay", delay)
                        .method("itemprototype.update")
                        .build();
                JsonObject delayObj = call(request_delay);
            }
        }
    }


    @Override
    public Map getItems(Search searchKey) throws NullPointerException {
        Request request = RequestBuilder.newBuilder().paramEntry("host", webZabbixRequest.getZabbixHostname())
                .paramEntry("search", searchKey.getParams())
                .paramEntry("searchWildcardsEnabled",true)
                .method("item.get").build();
        JsonObject response = call(request);
        JsonElement resultElement = response.get("result");
        if (resultElement == null) return null;
        JsonArray result = response.get("result").getAsJsonArray();

        Iterator iterator = result.iterator();
        Map items = new HashMap<>();
        while (iterator.hasNext()) {
            JsonObject element = (JsonObject) iterator.next();
            if(!element.get("delay").getAsString().equals("1s")){
                Request request_delay=RequestBuilder.newBuilder()
                        .paramEntry("itemid",element.get("itemid").getAsString())
                        .paramEntry("delay","1s")
                        .method("item.update")
                        .build();
                JsonObject delayObj=call(request_delay);
            }
            if(element.get("state").getAsInt()==0) {
                List<String> properties = new ArrayList<>();
                properties.add(element.get("key_").getAsString());
                properties.add(element.get("lastclock").getAsString());
                items.put(properties, element.get("lastvalue").getAsString());
            }
        }

        if (!items.isEmpty()) {
            return items;
        }
        return null;

    }

    @Override
    public Map getItemsOnProcessCnt() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "proc.num*").build();
        Map items = getItems(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "process");
        zabbixInfluxService.insertEntity(items, tags, proMeasurement);
        return items;
    }

    @Override
    public Map getItemsOnInodeUsed() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "vfs.fs.inode[*,pused]").build();
        Map items = getItems(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "inode");
        zabbixInfluxService.insertEntity(items, tags, inodeMeasurement);
        return items;
    }

    @Override
    public Map getItemsOnDiskUsed() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "vfs.fs.size[*,pused]").build();
        Map items = getItems(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "disk used");
        zabbixInfluxService.insertEntity(items, tags, diskMeasurement);
        return items;
    }

    @Override
    public Map getItemsOnDiskWrite() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "vfs.dev.write*").build();
        Map items = getItems(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "disk write");
        zabbixInfluxService.insertEntity(items, tags, diskMeasurement);
        return items;
    }

    @Override
    public Map getItemsOnDiskRead() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "vfs.dev.read*").build();
        Map items = getItems(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "disk read");
        zabbixInfluxService.insertEntity(items, tags, diskMeasurement);
        return items;
    }

    @Override
    public Map getItemsOnMemoryUsed() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "vm.memory.size[pused]").build();
        Map items = getItems(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "memory used");
        zabbixInfluxService.insertEntity(items, tags, memoryMeasurement);
        return items;
    }

    @Override
    public Map getItemsOnNetIn() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "net.if.in*").paramEntry("error","Not sup*").build();
        Map items = getItems(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "net in");
        zabbixInfluxService.insertEntity(items, tags, netMeasurement);
        return items;
    }

    @Override
    public Map getItemsOnNetOut() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "net.if.out*").build();
        Map items = getItems(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "net out");
        zabbixInfluxService.insertEntity(items, tags, netMeasurement);
        return items;
    }

    @Override
    public Map getItemsOnCpuUsed() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "system.cpu.util[,user]").build();
        Map items = getItems(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "cpu used");
        zabbixInfluxService.insertEntity(items, tags, cpuMeasurement);
        return items;
    }

    @Override
    public Map getItemsOnCpuLoad() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "system.cpu.load*").build();
        Map items = getItems(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "cpu load");
        zabbixInfluxService.insertEntity(items, tags, cpuMeasurement);
        return items;
    }

    @Override
    public Map getHistory(Search searchKey) {
        Request request = RequestBuilder.newBuilder().paramEntry("host", webZabbixRequest.getZabbixHostname()).paramEntry("search", searchKey.getParams()).method("item.get").build();
        JsonObject response = call(request);
        JsonElement resultElement = response.get("result");
        if (resultElement == null) return null;
        JsonArray result = response.get("result").getAsJsonArray();

        Iterator iterator = result.iterator();
        Map items = new HashMap<>();

        while (iterator.hasNext()) {
            Calendar calendar_from = Calendar.getInstance();
            Calendar calendar_till = Calendar.getInstance();

            JsonObject element = (JsonObject) iterator.next();
            String itemid = element.get("itemid").getAsString();
            for (int day = 1; day <= 2; day++) {
                calendar_from.add(Calendar.DATE, (-1) * day);
                calendar_till.add(Calendar.DATE, (-1) * day + 1);
                Request request_history = RequestBuilder.newBuilder()
                        .paramEntry("itemid", itemid)
                        .paramEntry("history", 0)
                        .paramEntry("time_from", calendar_from.getTime().getTime() / 1000)
                        .paramEntry("time_till", calendar_till.getTime().getTime() / 1000)
                        .method("history.get").build();
                JsonObject response_history = call(request_history);
                JsonElement resultHistory = response_history.get("result");
                if (resultHistory == null) continue;
                JsonArray result_history = resultHistory.getAsJsonArray();

                Iterator iterator_history = result_history.iterator();
                while (iterator_history.hasNext()) {
                    JsonObject element_history = (JsonObject) iterator_history.next();
                    List<String> properties = new ArrayList<>();
                    properties.add(element.get("key_").getAsString());
                    properties.add(element_history.get("clock").getAsString());
                    items.put(properties, element_history.get("value").getAsString());
                }
            }
        }

        if (!items.isEmpty()) {
            return items;
        }
        return null;
    }

    @Override
    public Map getHistoryOnProcessCnt() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "proc.num").build();
        Map items = getHistory(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "process history");
        zabbixInfluxService.insertEntity(items, tags, proMeasurement);
        return items;
    }

    @Override
    public Map getHistoryOnInodeUsed() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "inode").paramEntry("key_", "pused").build();
        Map items = getHistory(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "inode history");
        zabbixInfluxService.insertEntity(items, tags, inodeMeasurement);
        return items;
    }

    @Override
    public Map getHistoryOnDiskUsed() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "vfs.fs.size").paramEntry("key_", "pused").build();
        Map items = getHistory(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "disk used history");
        zabbixInfluxService.insertEntity(items, tags, diskMeasurement);
        return items;
    }

    @Override
    public Map getHistoryOnDiskWrite() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "vfs.dev.write").build();
        Map items = getHistory(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "disk write history");
        zabbixInfluxService.insertEntity(items, tags, diskMeasurement);
        return items;
    }

    @Override
    public Map getHistoryOnDiskRead() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "vfs.dev.read").build();
        Map items = getHistory(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "disk read history");
        zabbixInfluxService.insertEntity(items, tags, diskMeasurement);
        return items;
    }

    @Override
    public Map getHistoryOnMemoryUsed() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "vm.memory.size[pused]").build();
        Map items = getHistory(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "memory used history");
        zabbixInfluxService.insertEntity(items, tags, memoryMeasurement);
        return items;
    }

    @Override
    public Map getHistoryOnNetIn() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "net.if.in").paramEntry("state",0).build();
        Map items = getHistory(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "net in history");
        zabbixInfluxService.insertEntity(items, tags, netMeasurement);
        return items;
    }

    @Override
    public Map getHistoryOnNetOut() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "net.if.out").build();
        Map items = getHistory(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "net out history");
        zabbixInfluxService.insertEntity(items, tags, netMeasurement);
        return items;
    }

    @Override
    public Map getHistoryOnCpuUsed() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "system.cpu.util[,user]").build();
        Map items = getHistory(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "cpu used history");
        zabbixInfluxService.insertEntity(items, tags, cpuMeasurement);
        return items;
    }

    @Override
    public Map getHistoryOnCpuLoad() {
        Search searchKey = SearchBuilder.newBuilder().paramEntry("key_", "system.cpu.load").build();
        Map items = getHistory(searchKey);
        Map<String, String> tags = new HashMap<>();
        tags.put("type", "cpu load history");
        zabbixInfluxService.insertEntity(items, tags, cpuMeasurement);
        return items;
    }

}
