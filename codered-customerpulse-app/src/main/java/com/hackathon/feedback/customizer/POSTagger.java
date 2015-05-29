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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import javax.sound.midi.Sequence;

import com.hackathon.feedback.util.ConnectionUtils;

import oracle.jdbc.OraclePreparedStatement;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

/**
 * Tests for the {@link POSTaggerME} class.
 */
public class POSTagger {
	// The SimpleTokenizer is thread safe
	  private SimpleTokenizer mTokenizer = SimpleTokenizer.INSTANCE;

	public  void setPOSTagger(String line,  int id) throws IOException {
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
				try {
					if(negArr[counter] != "X")
						insertParsedData(token, tagsArr[counter], negArr[counter],id, (10000 *id+counter));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
	
	public static void insertRecord(String input_text, int id)
			throws SQLException {
		
		System.out.println("Input insertRecord" + input_text);
		Connection conn = ConnectionUtils.getConnection();
		conn.setAutoCommit(false);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO INPUT_DATA_REQUEST (input_data_request_oid, input_text, TIMESTAMP) ");
		sb.append(" VALUES (?,?, ?)");
		System.out.println(sb.toString());
		PreparedStatement ps = conn.prepareStatement(sb.toString());
		// Change batch size for this statement to 3
		((OraclePreparedStatement) ps).setExecuteBatch(1);
		ps.setInt(1, id);
		ps.setString(2, input_text);
		ps.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
		ps.executeUpdate();
		((OraclePreparedStatement) ps).sendBatch(); // JDBC sends the queued
		conn.commit();
		ps.close();
	}
	
	public static void insertParsedData(String word, String pos, String negation, int parentId, int id)
			throws SQLException {
		
		System.out.println("Input insertParsedData" + word + pos + negation + parentId + id);
		Connection conn = ConnectionUtils.getConnection();
		conn.setAutoCommit(false);
		StringBuilder sb = new StringBuilder();
		
		sb.append(" INSERT INTO INPUT_PARSED_DATA (input_parsed_data_oid, input_data_request_oid, token_string,  pos_tag, negation) ");
		sb.append(" VALUES (?,?, ?, ? , ?)");
		System.out.println(sb.toString());
		
		PreparedStatement ps = conn.prepareStatement(sb.toString());
		// Change batch size for this statement to 3
		((OraclePreparedStatement) ps).setExecuteBatch(1);
		ps.setInt(1, id);
		ps.setInt(2, parentId);
		ps.setString(3,word.toUpperCase());
		ps.setString(4,pos);
		ps.setString(5,negation);
		
		ps.executeUpdate();
		((OraclePreparedStatement) ps).sendBatch(); // JDBC sends the queued
		conn.commit();
		ps.close();
	}
	
	
	
	public static void delteRecord()
			throws SQLException {
		System.out.println(" delteRecord" );
		Connection conn = ConnectionUtils.getConnection();
		conn.setAutoCommit(false);
		
		PreparedStatement preparedStatement = conn.prepareStatement("DELETE from INPUT_PARSED_DATA");
		preparedStatement.executeUpdate();
		
		 preparedStatement = conn.prepareStatement("DELETE from INPUT_DATA_REQUEST");
		
		 preparedStatement.executeUpdate();
		
		conn.commit();
		
		preparedStatement.close();
		
		System.out.println(" dELETED" );
	}
	
	public static void main(String[] args) {
		try {
			new POSTagger().setPOSTagger("This isn't fun", 12);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}