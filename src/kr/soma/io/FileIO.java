/**
 * 모델 및 학습 데이터 파일 저장 및 불러오기 담당 패키지 
 */
package kr.soma.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import kr.soma.filter.Direction;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import be.ac.ulg.montefiore.run.jahmm.io.HmmReader;
import be.ac.ulg.montefiore.run.jahmm.io.HmmWriter;
import be.ac.ulg.montefiore.run.jahmm.io.OpdfDiscreteReader;
import be.ac.ulg.montefiore.run.jahmm.io.OpdfDiscreteWriter;
/**
 * 모델 및 학습 데이터를 파일에 저장 및 불러오는 클래스 
 * @author limjunsung
 *
 */
public class FileIO {
	/**
	 * 모델 파일 저장 메소드 
	 * @param hmms 모델 
	 */
	public void saveHMMData(Map<String, Hmm<ObservationDiscrete<Direction>>> hmms) {
		File dir = new File(android.os.Environment.getExternalStorageDirectory(), "GestureController"+File.separator+"HMM");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		try {
			String fileName = null;
			FileWriter writer = null;
			for (String modelName : hmms.keySet()) {
				fileName = modelName + ".model";
				File f = new File(dir+File.separator + fileName);
				writer = new FileWriter(f);
				HmmWriter.write(writer, new OpdfDiscreteWriter<Direction>(Direction.class), hmms.get(modelName));
				writer.flush();
			}
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 모델을 파일로 부터 불러들임 
	 * @param modelNames 모델명들 
	 * @return 생성된 모델 객체들 
	 */
	public Map<String, Hmm<ObservationDiscrete<Direction>>> loadHMMData(List<String> modelNames) {
		File hmmDir = new File(android.os.Environment.getExternalStorageDirectory(), "GestureController"+File.separator+"HMM");
		String fileName = null;
		Map<String, Hmm<ObservationDiscrete<Direction>>> hmms = new HashMap<String, Hmm<ObservationDiscrete<Direction>>>();
		Hmm<ObservationDiscrete<Direction>> hmm = null;
		
		try {
			BufferedReader in = null;
			if (!hmmDir.exists()) {
				throw new Exception("GestureController"+File.separator+"HMM folder does not exist!" );
			}
			
			if (modelNames != null) {
				for (String modelName : modelNames) {
					fileName = modelName + ".hmm";
					File f = new File(hmmDir+File.separator + fileName);
					in = new BufferedReader(new FileReader(f)); 

					hmm = HmmReader.read(in, new OpdfDiscreteReader<Direction>(Direction.class));
					hmms.put(modelName, hmm);
				}
			}
			else {
				File []fileList=hmmDir.listFiles();
				for(File tempFile : fileList) {
					if (tempFile.isFile()) {
						String tempFileName = tempFile.getName();
						File f = new File(hmmDir + File.separator + tempFileName);
						in = new BufferedReader(new FileReader(f));

						hmm = HmmReader.read(in, new OpdfDiscreteReader<Direction>(Direction.class));
						hmms.put(tempFileName.substring(0, tempFileName.length() - 6), hmm);
					}
				}
			}
			in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return hmms;
	}
	
	/**
	 * 파일로 부터 학습 데이터를 불러옴 
	 * @param modelNames 불러올 학습 데이터 이름들  
	 * @return 생성된 학습 데이터들 
	 */
	public Map<String, List<List<Direction>>> readObservationsSequencesFromFiles(String[] modelNames) {
		String buffer;
		Map<String, List<List<Direction>>> result = new HashMap<String, List<List<Direction>>>();
		List<List<Direction>> listOfDirList = new ArrayList<List<Direction>>();
		File learningDataDir = new File(android.os.Environment.getExternalStorageDirectory(), "GestureController"+File.separator+"LearningData");
		try {
			BufferedReader in = null;
			if (modelNames != null) {
				for(String modelName : modelNames) {
					File f = new File(learningDataDir+File.separator + modelName + ".lrn");
					in = new BufferedReader(new FileReader(f));
					while( (buffer = in.readLine()) != null ) {
						List<Direction> dirList = new ArrayList<Direction>();
						StringTokenizer tokenizer = new StringTokenizer(buffer, " ");
						while(tokenizer.hasMoreTokens()){
							dirList.add(Direction.valueOf(tokenizer.nextToken()));
						}
						listOfDirList.add(dirList);
					}
					result.put(modelName, listOfDirList);
					listOfDirList = new ArrayList<List<Direction>>();
				}
			}
			else {
				File []fileList = learningDataDir.listFiles();
				for(File tempFile : fileList) {
					if (tempFile.isFile()) {
						String tempFileName = tempFile.getName();
						File f = new File(learningDataDir+File.separator + tempFileName);
						in = new BufferedReader(new FileReader(f));
						while( (buffer = in.readLine()) != null ) {
							List<Direction> dirList = new ArrayList<Direction>();
							StringTokenizer tokenizer = new StringTokenizer(buffer, " ");
							while(tokenizer.hasMoreTokens()){
								dirList.add(Direction.valueOf(tokenizer.nextToken()));
							}
							listOfDirList.add(dirList);
						}
						result.put(tempFileName.substring(0, tempFileName.length() - 4), listOfDirList);
						listOfDirList = new ArrayList<List<Direction>>();
					}
				}
			}
			in.close();			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return result;
	}
	/**
	 * 학습 데이터를 파일에 저장 
	 * @param modelName 학습 데이터명들 
	 * @param learningData 학습 데이터 
	 */
	public void writeObservationsSequencesToFile(String modelName, List<List<Direction>> learningData) {
		try {
			File learningDataDir = new File(android.os.Environment.getExternalStorageDirectory(), "GestureController"+File.separator+"LearningData");
			File f = new File(learningDataDir+File.separator + modelName + ".lrn");
			FileWriter writer;
			if (f.exists()) 
				writer = new FileWriter(f, true);
			else
				writer = new FileWriter(f);
			for (List<Direction> dirList : learningData) {
				for (Direction dir : dirList) {
					writer.write(dir.toString() + " ");
				}
				writer.write("\n");
			}
			writer.flush();
			writer.close();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
