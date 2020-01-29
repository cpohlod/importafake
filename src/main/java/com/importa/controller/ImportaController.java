package com.importa.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.importa.bo.Log;
import com.importa.error.ImportaNotFoundException;
import com.importa.repo.LogRepository;

@RestController
public class ImportaController {

    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String CONTENT_TYPE = "Content-Type";
	
    @Autowired
    private LogRepository logRepo;
    
    protected static ConcurrentHashMap<String, String> securityParams = new ConcurrentHashMap<>();
	
    @RequestMapping(value = "importa-ind", method = RequestMethod.POST)	
	@CrossOrigin(maxAge = 3600)
	@ResponseBody
	public void importar(HttpServletRequest request, HttpServletResponse response, @RequestBody String json) {
		try {
			//jsonToObjeto(json);
			Thread thBranca = new Thread(bolinhaBranca);
			thBranca.start();
			String token = geraToken();
			securityParams.put(token, json);
			writeResponse(response, token);
		} catch (Exception e) {
			throw new ImportaNotFoundException("Não foi possível importar");
		}
	}
    
	private String geraToken() {
		return DigestUtils.sha256Hex("teste");
	}

	private void jsonToObjeto(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.readValue(json, Map.class);
		System.out.println((String)map.get("forma"));
		System.out.println((String)map.get("justica"));
		System.out.println((String)map.get("sala"));
		List<String> processos = (List)map.get("processos");
		for (String p : processos) {
			System.out.println(p);
		}
	}
	
    private void writeResponse(HttpServletResponse response, String html) throws IOException {
		response.getOutputStream().write(html.getBytes(Charset.forName("UTF-8")));
		response.setHeader(CONTENT_TYPE, "text/html");
		response.setHeader(CONTENT_DISPOSITION, "inline");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.flushBuffer();
	}

    
    // FindAll logs
    @GetMapping("/logs/{token}")
	@CrossOrigin(maxAge = 3600)
    public List<Log> findAllLogs(@PathVariable String token) {
    	String jsonSecurity = securityParams.get(token);
		if (jsonSecurity==null) {
			throw new ImportaNotFoundException("Token'"+token+"', inválido");
		}
		List<Log> retornoLogs = new ArrayList<Log>();
		List<Log> logs = new ArrayList<Log>((Collection<? extends Log>) logRepo.findAll());
		
		for(int i=0;i<100;i++) {
			Log log = logs.get(i); 
			retornoLogs.add(log);
			logRepo.deleteById(new Long(i));
		}
        return retornoLogs;
    }
    
    @RequestMapping(value = "getProcessamento", method = RequestMethod.GET)
	@CrossOrigin(maxAge = 3600)
	public void fechaVersao(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String token = request.getParameter("token");
    	String jsonSecurity = securityParams.get(token);
		if (jsonSecurity==null) {
			throw new ImportaNotFoundException("Token'"+token+"', inválido");
		}
		List<Log> retornoLogs = new ArrayList<Log>();
		List<Log> logs = new ArrayList<Log>((Collection<? extends Log>) logRepo.findAll());
		
		for(int i=0;i<10 && i<logs.size();i++) {
			Log log = logs.get(i); 
			retornoLogs.add(log);
		}
		for(Log log : retornoLogs) {
			if (logRepo.existsById(log.getId())) {
				System.out.println("tenta deletar");
				logRepo.deleteById(log.getId());
				System.out.println("deletou");				
			}
		}
			
		ObjectMapper Obj = new ObjectMapper(); 
		String json = Obj.writeValueAsString(retornoLogs); 
		writeResponse(response, json);
	}
    
    Runnable bolinhaBranca = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                //try {
                    System.out.println("BRANCA");
       	        	logRepo.save(new Log("importação do processo nr."+i+"...", "INFO", new Long(i)));
                    //Thread.sleep(1000); 
                //} catch (InterruptedException ex) {
                    System.out.println("A Thread sofreu uma interrupcao!");
                //}
            }
        }
    };

}
