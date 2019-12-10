package com.central.controller;

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

import com.central.bo.Log;
import com.central.error.CentralNotFoundException;
import com.central.repo.LogRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class CentralController {

    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String CONTENT_TYPE = "Content-Type";
	
    @Autowired
    private LogRepository logRepo;
    
    protected static ConcurrentHashMap<String, String> securityParams = new ConcurrentHashMap<>();
	
    @RequestMapping(value = "importaInd", method = RequestMethod.POST)	
	@CrossOrigin(maxAge = 3600)
	@ResponseBody
	public void importar(HttpServletRequest request, HttpServletResponse response, @RequestBody String json) {
		try {
			jsonToObjeto(json);
			String token = geraToken();
			securityParams.put(token, json);
			writeResponse(response, token);
		} catch (Exception e) {
			throw new CentralNotFoundException("Não foi possível fazer o login");
		}
	}
    
	private String geraToken() {
		return DigestUtils.sha256Hex("teste");
	}

	private void jsonToObjeto(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.readValue(json, Map.class);
		System.out.println((String)map.get("name"));
		System.out.println((String)map.get("email"));
		System.out.println((String)map.get("pwd"));
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
    public List<Log> findAllLogs(@PathVariable String token) {
        return new ArrayList<Log>((Collection<? extends Log>) logRepo.findAll());
    }
    
    
}
