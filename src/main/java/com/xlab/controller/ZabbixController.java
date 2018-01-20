package com.xlab.controller;

import com.xlab.entity.bean.CsvBean;
import com.xlab.entity.bean.WebInfluxRequest;
import com.xlab.entity.bean.WebItemRequest;
import com.xlab.entity.bean.WebZabbixRequest;
import com.xlab.service.ZabbixInfluxService;
import com.xlab.service.ZabbixService;
import io.swagger.annotations.ApiOperation;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@CrossOrigin
@RestController
public class ZabbixController {

    @Autowired
    private ZabbixService zabbixService;

    @Autowired
    private ZabbixInfluxService zabbixInfluxService;

    private CsvBean csvBean;
    private boolean zabbixConnected=false;
    private boolean influxConnected=false;

    @ApiOperation(value = "getZabbixConfig", notes = "Get configuration of zabbix from user input")
    @GetMapping("zabbix")
    public String getZabbixConfig(Model model){
        model.addAttribute("webZabbixRequest",new WebZabbixRequest());
        return "zabbixconfig";
    }

    @ApiOperation(value = "zabbixConnect",notes = "Handle the post from zabbix")
    @PostMapping("zabbix")
    @ResponseBody
    public String zabbixConnect(@RequestBody WebZabbixRequest webZabbixRequest){
        zabbixService.setZabbixRequestSource(webZabbixRequest);
        zabbixConnected=zabbixService.login();
        if (zabbixConnected) {
            return "Success";
        } else {
            return "Failed";
        }
    }

    @ApiOperation(value = "getInfluxConfig",notes = "Get configuration of influxdb from user input")
    @GetMapping("influx")
    public String getInfluxConfig(Model model){
        model.addAttribute("webInfluxRequest",new WebInfluxRequest());
        return "influxdbconfig";
    }

    @ApiOperation(value = "influxConnect",notes = "Handle the post from influx")
    @PostMapping("influx")
    @ResponseBody
    public String influxConnect(@RequestBody WebInfluxRequest webInfluxRequest){
        zabbixInfluxService.setInfluxRequestSource(webInfluxRequest);
        influxConnected=zabbixInfluxService.connect();
        if(influxConnected){
            return "Success";
        }
        else {
            return "Failed";
        }
    }

    @ApiOperation(value = "getItemToSearch",notes = "Get the measurement user wants to search")
    @GetMapping("search")
    public String getItemToSearch(Model model){
        model.addAttribute("webItemRequest",new WebItemRequest());
        return "csvconfig";
    }

    @ApiOperation(value = "transformCsv",notes = "Load the data of measurement into a javaBean")
    @PostMapping("search")
    @ResponseBody
    public CsvBean transformCsv(@RequestBody WebItemRequest webItemRequest){
        CsvBean csvBean=zabbixInfluxService.readData(webItemRequest.getItem());

        // create file
        List<String> columns=csvBean.getColumns();
        List<List<Object>> values=csvBean.getValues();
        try{
            String dirName="/tmp/zabbix";
            File dir=new File(dirName);
            if(!dir.exists()){
                dir.mkdir();
            }
            File csv=new File(dirName+"/"+csvBean.getName()+".csv");
            if(!csv.exists()){
                csv.createNewFile();
            }
            BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(csv,true));
            for(int i=0;i<columns.size();i++){
                bufferedWriter.write(columns.get(i).replace(",","."));
                bufferedWriter.write(',');
            }
            bufferedWriter.newLine();
            for(int j=0;j<values.size();j++){
                bufferedWriter.write(values.get(j).toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return csvBean;
    }

    @ApiOperation(value = "csvShow",notes = "Show the measurement as .csv files")
    @GetMapping("csvshow")
    public String csvShow(Model model){
        if(this.csvBean!=null) {
            model.addAttribute("csvBean", this.csvBean);
            return "csvshow";
        }
        else {
            return "csverror";
        }
    }


    @ApiOperation(value = "download",notes = "Download data as .csv files")
    @GetMapping("download")
    public void downloadCsv(@RequestParam("fileName") String originFilename, HttpServletResponse response) {

        try {
            final String filePath = "/tmp/zabbix/" + originFilename + ".csv";

            File file = new File(filePath);

            // fix chinese filename problem
            String filename = new String(originFilename.getBytes("utf-8"), "iso-8859-1");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + filename + ".csv" + "\"");

            byte[] buff = new byte[1024];
            BufferedInputStream bis = null;
            OutputStream os = null;
            try {
                os = response.getOutputStream();
                bis = new BufferedInputStream(new FileInputStream(file));
                int i = bis.read(buff);
                while (i != -1) {
                    os.write(buff, 0, buff.length);
                    i = bis.read(buff);
                }
                os.flush();
            } finally {
                if (bis != null) {
                    bis.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
