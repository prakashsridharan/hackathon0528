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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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
		//((OraclePreparedStatement) ps).setExecuteBatch(1);
		ps.setInt(1, id);
		ps.setString(2, input_text);
		ps.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
		ps.executeUpdate();
		//((OraclePreparedStatement) ps).sendBatch(); // JDBC sends the queued
		conn.commit();
		ps.close();
		conn.close();
	}
	
	public static void insertParsedData(String word, String pos, String negation, int parentId, int id)
			throws SQLException {
		
		System.out.println("Input insertParsedData" + word + pos + negation + parentId + id);
		Connection conn = ConnectionUtils.getConnection();
		
		if(conn == null) {
			
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
			} catch (ClassNotFoundException e) {
				System.out.println("Where is your Oracle JDBC Driver?");
				e.printStackTrace();
			}
			System.out.println("Oracle JDBC Driver Registered!");
			try {
				conn = DriverManager.getConnection(
						"jdbc:oracle:thin:@localhost:1521:xe", "test", "test");
			} catch (SQLException e) {
				System.out.println("Connection Failed! Check output console");
				e.printStackTrace();
			}
		}
		
		
		conn.setAutoCommit(false);
		StringBuilder sb = new StringBuilder();
		
		sb.append(" INSERT INTO INPUT_PARSED_DATA (input_parsed_data_oid, input_data_request_oid, token_string,  pos_tag, negation) ");
		sb.append(" VALUES (?,?, ?, ? , ?)");
		System.out.println(sb.toString());
		
		PreparedStatement ps = conn.prepareStatement(sb.toString());
		// Change batch size for this statement to 3
	//	((OraclePreparedStatement) ps).setExecuteBatch(1);
		ps.setInt(1, id);
		ps.setInt(2, parentId);
		ps.setString(3,word.toUpperCase());
		ps.setString(4,pos);
		ps.setString(5,negation);
		
		ps.executeUpdate();
		//((OraclePreparedStatement) ps).sendBatch(); // JDBC sends the queued
		conn.commit();
		ps.close();
		conn.close();
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
		conn.close();
		System.out.println(" dELETED" );
	}
	
	public static void generateOutput() throws SQLException {
		System.out.println(" generateOutput" );
		 Connection conn = ConnectionUtils.getConnection();
		 Charset charset = Charset.forName("US-ASCII");
		  Statement  stmt = null;
		  String sql = "SELECT output_response FROM INPUT_DATA_REQUEST";
		  stmt = conn.createStatement();
	      ResultSet rs = stmt.executeQuery(sql);
	      //STEP 5: Extract data from result set
	      
	      StringBuilder outStr = new StringBuilder();
	      while(rs.next()){
	    	  System.out.println(rs.getString(1));
	    	  outStr.append(rs.getString(1)).append(
						System.getProperty("line.separator"));
	      }
	      
	      Path outFilePath = Paths
					.get("D:\\github\\hackathon0528\\codered-customerpulse-app\\src\\main\\resources\\codered",
							"output.txt");
			try (BufferedWriter writer = Files.newBufferedWriter(outFilePath,
					charset)) {
				writer.write(outStr.toString(), 0, outStr.toString().length());
			} catch (IOException x) {
				System.err.format("IOException: %s%n", x);
			}
	}
	
	public static void callStoredProcedure() throws SQLException {
		System.out.println(" callStoredProcedure" );
		Connection conn = ConnectionUtils.getConnection();
		 CallableStatement cStmt = conn.prepareCall("{call nlp_engine(?)}");
		 cStmt.registerOutParameter(1, Types.INTEGER);
		 cStmt.executeUpdate();
		int outputCode =  cStmt.getInt(1);
		cStmt.close();
		conn.close();
System.out.println(" callStoredProcedure" + outputCode);
	}
}