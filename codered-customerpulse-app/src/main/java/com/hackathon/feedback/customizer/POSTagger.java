/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hackathon.feedback.customizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

/**
 * Tests for the {@link POSTaggerME} class.
 */
public class POSTagger {
	// The SimpleTokenizer is thread safe
	  private SimpleTokenizer mTokenizer = SimpleTokenizer.INSTANCE;

	public void setPOSTagger(String line) throws IOException {
		InputStream modelIn = null;
		try {
			// Loading tokenizer model
			modelIn = getClass().getResourceAsStream("en-pos-maxent.bin");
			final POSModel posModel = new POSModel(modelIn);
			//modelIn.close();
			POSTaggerME tagger = new POSTaggerME(posModel);
			String tokensArr[] =  mTokenizer.tokenize(line);
			String tagsArr[] = tagger.tag(tokensArr);
			String negArr[] = new String[tagsArr.length];
			Arrays.fill(negArr, "N");
			// X - Dont insert, Y - Negation true ,N - negation false
			int counter  =0;
			for(String token : tokensArr) {
				if(token != null && token != "" && ("not".equalsIgnoreCase(token) || "t".equalsIgnoreCase(token))) {
					if(negArr.length > counter ) {
						negArr[counter] = "X";
						negArr[counter+1] = "Y";
					}
				}
				System.out.println(token +"-" + tagsArr[counter]);
				counter++;
			}
			System.out.println("tokensArr" +Arrays.asList(tokensArr).toString());
			System.out.println("tagsArr"+Arrays.asList(tagsArr).toString());
			System.out.println("negArr"+Arrays.asList(negArr).toString());
			
		} catch (final IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (final IOException e) {
				} // oh well!
			}
		}

	}
	
	public static void main(String[] args) {
		try {
			new POSTagger().setPOSTagger("This isn't fun");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}