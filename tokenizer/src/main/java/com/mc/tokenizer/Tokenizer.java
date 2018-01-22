package com.mc.tokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.hankcs.hanlp.HanLP;
import com.hankcs.lucene.HanLPAnalyzer;
import com.mc.entity.MCTerm;
import com.spreada.utils.chinese.ZHConverter;

@Controller
public class Tokenizer {

	@RequestMapping(value = "/parse", method = RequestMethod.POST)
	public ResponseEntity<?> parse(@RequestParam("content") String content) {

		String stext = content;
		try {
			ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
			ZHConverter tradConverter = ZHConverter.getInstance(ZHConverter.TRADITIONAL);
			String text = converter.convert(stext);
		
			//System.out.print(text);
			boolean isTrad = false;
			if(!text.equals(stext)) {
				isTrad = true;
			}
			Analyzer analyzer = new StandardAnalyzer();
			TokenStream tokenStream = analyzer.tokenStream("field", text);
			tokenStream.reset();
			List<MCTerm> standardList = new ArrayList<MCTerm>();
			while (tokenStream.incrementToken()) {
				CharTermAttribute attribute = tokenStream.getAttribute(CharTermAttribute.class);
				// 偏移量
				OffsetAttribute offsetAtt = tokenStream.getAttribute(OffsetAttribute.class);
				
				String token = attribute.toString();
				if(!token.equals(" ")) {
					if(isTrad) {
						token = tradConverter.convert(token);
					}
					MCTerm term = new MCTerm(token, offsetAtt.startOffset(), offsetAtt.endOffset()-1);
					standardList.add(term);
				}
				
			}
			analyzer.close();
			
			Analyzer hanlpAnalyzer = new HanLPAnalyzer();
			TokenStream htokenStream = hanlpAnalyzer.tokenStream("field", text);
			
			htokenStream.reset();
			List<MCTerm> hanlpList = new ArrayList<MCTerm>();
			while (htokenStream.incrementToken()) {
				CharTermAttribute hattribute = htokenStream.getAttribute(CharTermAttribute.class);
				// 偏移量
				OffsetAttribute hoffsetAtt = htokenStream.getAttribute(OffsetAttribute.class);
				
				String token = hattribute.toString();
				if(!token.equals(" ")) {
					if(isTrad) {
						token = tradConverter.convert(token);
					}
					MCTerm hterm = new MCTerm(token, hoffsetAtt.startOffset(), hoffsetAtt.endOffset()-1);
					hanlpList.add(hterm);
				}
				
			}
			hanlpAnalyzer.close();
			List<MCTerm> resList = new ArrayList<MCTerm>();
			int i = 0;
			int j = 0;
			while(i < standardList.size() && j  < hanlpList.size()) {
				if(standardList.get(i).getStart() == hanlpList.get(j).getStart()) {
					if(standardList.get(i).getEnd() < hanlpList.get(j).getEnd()) {
						resList.add(standardList.get(i));
						resList.add(hanlpList.get(j));
						i++;
						j++;
					}else if(standardList.get(i).getEnd() == hanlpList.get(j).getEnd()) {
						resList.add(standardList.get(i));
						i++;
						j++;
					}else {
						resList.add(hanlpList.get(j));
						resList.add(standardList.get(i));
						i++;
						j++;
					}
				}else if(standardList.get(i).getStart() < hanlpList.get(j).getStart()) {
					resList.add(standardList.get(i));
					i++;
				}else {
					resList.add(hanlpList.get(j));
					j++;
				}
			}
			
			for(; i < standardList.size(); i++) {
				resList.add(standardList.get(i));
			}
			
			for(; j < hanlpList.size(); j++) {
				resList.add(hanlpList.get(j));
			}
			Map<String, Object> res = new HashMap<String, Object>();
			res.put("termList", resList);
			return new ResponseEntity<Object>(res, new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Have exception", new HttpHeaders(), HttpStatus.BAD_REQUEST);
		}

	}
}
