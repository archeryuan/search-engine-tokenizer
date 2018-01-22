package com.mc.tokenizer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.hankcs.lucene.HanLPAnalyzer;

@SpringBootApplication
public class TokenizerApplication {
	public static List<String> parseKeywords(Analyzer analyzer, String field, String keywords) {

        List<String> result = new ArrayList<String>();
        TokenStream stream  = analyzer.tokenStream(field, new StringReader(keywords));
        OffsetAttribute offsetAttribute = stream.addAttribute(OffsetAttribute.class);
        CharTermAttribute charTermAttribute = stream.addAttribute(CharTermAttribute.class);


        try {
            while(stream.incrementToken()) {
               
               int startOffset = offsetAttribute.startOffset();
               int endOffset = offsetAttribute.endOffset();
               String term = charTermAttribute.toString();
               
               System.out.println("startOffset=="+startOffset + ", endOffset=="+endOffset + ", term=="+term);
            }
        }
        catch(IOException e) {
            // not thrown b/c we're using a string reader...
        }

        return result;
    }  

	public static void main(String[] args) throws Exception {
		System.out.println("xxxxx");
//		String text = "国泰航空 nice";
//		for (int i = 0; i < text.length(); ++i)
//		{
//		    System.out.print(text.charAt(i) + "" + i + " ");
//		}
//		System.out.println();
//		Analyzer analyzer = new StandardAnalyzer();
//		TokenStream tokenStream = analyzer.tokenStream("field", text);
//		tokenStream.reset();
//		while (tokenStream.incrementToken())
//		{
//		    CharTermAttribute attribute = tokenStream.getAttribute(CharTermAttribute.class);
//		    // 偏移量
//		    OffsetAttribute offsetAtt = tokenStream.getAttribute(OffsetAttribute.class);
//		    // 距离
//		    PositionIncrementAttribute positionAttr = tokenStream.getAttribute(PositionIncrementAttribute.class);
//		    // 词性
//		    TypeAttribute typeAttr = tokenStream.getAttribute(TypeAttribute.class);
//		    System.out.printf("[%d:%d %d] %s/%s\n", offsetAtt.startOffset(), offsetAtt.endOffset(), positionAttr.getPositionIncrement(), attribute, typeAttr.type());
//		}
//		analyzer.close();
		SpringApplication.run(TokenizerApplication.class, args);
	}
}
