/**
 * 3rd Party 앱과 연동을 위한 컨트롤러 패키지 
 */
package kr.soma.controller;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.soma.classifier.Analyzer;
import kr.soma.classifier.HMM;
import kr.soma.events.AccelerationEvent;
import kr.soma.events.AccelerationListener;
import kr.soma.events.AnalysisEvent;
import kr.soma.events.AnalysisListener;
import kr.soma.events.LearningEvent;
import kr.soma.events.LearningListener;
import kr.soma.events.OrientationListener;
import kr.soma.events.RecognitionEvent;
import kr.soma.events.RecognitionListener;
import kr.soma.filter.Direction;
import kr.soma.filter.DirectionalEquvalenceFilter;
import kr.soma.filter.Filter;
import kr.soma.filter.HighPassFilter;
import kr.soma.filter.LowPassFilter;
import kr.soma.filter.MotionDetectFilter;
import kr.soma.io.FileIO;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;

/**
 * 모션 인식을 제어하는 클래스 
 * @author limjunsung
 *
 */
public class GestureController implements SensorEventListener {
	/**
	 * 양자화를 마쳤다는 메시지 
	 */
	public final static int END_QUANTIZATION = 0;
	/**
	 * 모델링을 마쳤다는 메시지 
	 */
	public final static int END_MODELING = 1;
	/**
	 * 모델링을 성공적으로 수행중이라는 메시지 
	 */
	public final static int SUCCESS_MODELING = 2;
	/**
	 * 모션 인식을 시작했다는 메시지 
	 */
	public final static int START_RECOGNITION = 3;
	/**
	 * 모션 인식 분석을 마쳤다는 메시지 
	 */
	public final static int END_RECOGNITION = 4;
	/**
	 * 모션을 분석하기에 현재 수집한 데이터가 너무 작다는 메시지   
	 */
	public final static int NOT_ENOUGH_DATA = 5;
	/**
	 * 학습 데이터 수집을 완료했다는 메시지 
	 */
	public final static int END_LEARNING = 6;
	/**
	 * 학습 데이터 수집을 시작했다는 메시지 
	 */
	public final static int START_LEARNING = 7;
	/**
	 * 가속도 센서 사용 가능 여부 
	 */
	private boolean isAccelerationEnabled;
	/**
	 * 방향 센서 사용 가능 여부 
	 */
	private boolean isOrientationEnabled;
	/**
	 * 현재 학습 데이터 수집 중인지 여부 
	 */
	private boolean isLearning;
	/**
	 * 현재 모션 분석 중인지 여부 
	 */
	private boolean isRecognizing;
	/**
	 * 현재 모션 분석 모드인지 여부 
	 */
	private boolean isRecognitionMode;
	/**
	 * 등록된 이벤트 리스너들을 가지고 있는 리스트 
	 */
	private List<EventListener> listenerList;
	/**
	 * 모션 인식의 정확도를 높이기 위한 각종 필터 클래스를 가지고 있는 리스트 
	 */
	private List<Filter> filters;
	/**
	 * 모션 분석 클래스 
	 */
	private Analyzer analyzer;
	/**
	 * 가속도 센서값을 가지고 있는 리스트 
	 */
	private List<AccelerationEvent> accelerationList;
	/**
	 * 여러 개의 학습 데이터에 대한 양자화된 정보를 가지고 있는 리스트
	 */
	private List<List<Direction>> listOfAccList;
	/**
	 * 모델명과 모델 정보를 가지고 있는 맵 
	 */
	private Map<String, HMM> modelList = new HashMap<String, HMM>();
	/**
	 * 모션의 시작과 끝을 찾아주는 클래스 
	 */
	private MotionDetectFilter motionDetectFilter;
	/**
	 * 같은 방향에 대한 연속적인 데이터를 처리해주는 클래스 
	 */
	private DirectionalEquvalenceFilter directionalEquvalenceFilter;
	/**
	 * 모델 및 학습 데이터를 파일에 저장 및 불러오는 클래스 
	 */
	private FileIO fileIO;
	/**
	 * 모션 시작에 대한 민감도 
	 */
	private double idleStateSensitivy = 1;
	/**
	 * 같은 방향에 대한 민감도 
	 */
	private double directioinSensitivy = 2;
	/**
	 * 모션 분석을 위해 필요한 최소 데이터 갯수 
	 */
	private int dataLimit = 16;
	/**
	 * 가속도 센서값을 저장하고 있을지 버릴지 판단하는 변수 
	 */
	private boolean collectAcc = false;
	/**
	 * 모션의 끝 
	 */
	private int cutIdx = -1;
	/**
	 * 생성자 
	 */
	public GestureController() {
		listOfAccList = new ArrayList<List<Direction>>();
		accelerationList = new ArrayList<AccelerationEvent>();
		isAccelerationEnabled = true;
		isOrientationEnabled = true;
		isLearning = false;
		isRecognizing = false;
		isRecognitionMode = false;
		fileIO = new FileIO();
		listenerList = new ArrayList<EventListener>();
		motionDetectFilter = new MotionDetectFilter(this, idleStateSensitivy, 40);
		directionalEquvalenceFilter = new DirectionalEquvalenceFilter(this, directioinSensitivy);
		filters = new ArrayList<Filter>();
		filters.add(new LowPassFilter());
		filters.add(new HighPassFilter());
		filters.add(motionDetectFilter);
		filters.add(directionalEquvalenceFilter);
		analyzer = new Analyzer();
	}

	/**
	 * 가속도센서에 대한 이벤트 발생   
	 * @param event 가속도센서 이벤트 
	 */
    private void fireAccelerationEvent(AccelerationEvent event) {
        for (int i = 0; i < listenerList.size(); i++){
            if (listenerList.get(i) instanceof AccelerationListener){
                AccelerationListener listener = (AccelerationListener)listenerList.get(i);
                listener.doAccelerationProcess(event);
            }
        }
        
        if (collectAcc) {
        	accelerationList.add(event);
        }
    }
    
    /**
     * 학습데이터 모델링에 대한 이벤트 발생  
     * @param event 모델링 이벤트  
     */
    private void fireAnalysisEvent(AnalysisEvent event) {
        for (int i = 0; i < listenerList.size(); i++){
            if (listenerList.get(i) instanceof AnalysisListener){
                AnalysisListener listener = (AnalysisListener)listenerList.get(i);
                listener.doAnalysisProcess(event);
            }
        }
    }
    
    /**
     * 모션 분석에 대한 이벤트 발생 
     * @param event 모션 분석 이벤트 
     */
    private void fireRecognitionEvent(RecognitionEvent event) {
        for (int i = 0; i < listenerList.size(); i++){
            if (listenerList.get(i) instanceof RecognitionListener){
                RecognitionListener listener = (RecognitionListener)listenerList.get(i);
                listener.doRecognitionProcess(event);
            }
        }
    }
    /**
     * 학습에 대한 이벤트 발생 
     * @param event 학습 이벤트 
     */
    private void fireLearningEvent(LearningEvent event) {
    	for (int i = 0; i < listenerList.size(); i++){
            if (listenerList.get(i) instanceof LearningListener){
                LearningListener listener = (LearningListener)listenerList.get(i);
                listener.doLearningProcess(event);
            }
        }
    }
    
    /**
     * 모델을 파일에 저장  
     * @param learnedModel 모델 리스트 
     */
    public void saveHmm(Map<String, HMM> learnedModel) {
    	Map<String, Hmm<ObservationDiscrete<Direction>>> hmms = new HashMap<String, Hmm<ObservationDiscrete<Direction>>>();
    	for (String modelName : learnedModel.keySet()) {
    		hmms.put(modelName, learnedModel.get(modelName).getHmm());
    	}
    	fileIO.saveHMMData(hmms);
    }
    
    /**
     * 학습 데이터 수집 시작 
     */
    public void startLearning() {
    	if (isLearning || isRecognitionMode) {
    		Log.i("GestureController", "startLearning() is fail!!");
    		return;
    	}
    	Log.i("GestureController", "learning starts!!");
    	isLearning = true;
    	collectAcc = true;
    }
    /**
     * 학습 데이터 수집 
     */
    public void finishLearning() {
    	if (!isLearning || isRecognitionMode) {
    		Log.i("GestureController", "finishLearning() is fail!!");
    		return;
    	}
    	collectAcc = false;
    	if (motionDetectFilter.isDelayTime()) {
    		cutAccelerationList();
    		motionDetectFilter.enableDelayTime(false);
    		Log.i("MotionDetectFilter", "강제로 컷 ");
    	}
    	if (accelerationList.size() < dataLimit) {
    		fireLearningEvent(new LearningEvent(this, NOT_ENOUGH_DATA, null, null));
    		return;
    	}
    	
    	GeneralHandler qHandler = new GeneralHandler();
    	QuantizerThread qThread = new QuantizerThread(qHandler, new ArrayList(accelerationList));
    	qThread.run();
    	accelerationList = new ArrayList<AccelerationEvent>();
    }
    /**
     * 모션 분석 시작
     */
    public void startRecognition() {
    	if (!isRecognitionMode || isLearning) {
    		Log.i("GestureController", "startRecognition() is fail!!");
    		return;
    	}
    	Log.i("GestureController", "Start Recognition");
    	collectAcc = true;
    	fireRecognitionEvent(new RecognitionEvent(this, START_RECOGNITION, null, null));
    }
    /**
     * 모션 분석 끝 
     */
    public void finishRecognition() {
    	if (!isRecognitionMode || isLearning) {
    		Log.i("GestureController", "finishRecognition() is fail!!");
    		return;
    	}
    	Log.i("GestureController", "finish Recognition");
    
    	collectAcc = false;
    	if (motionDetectFilter.isDelayTime()) {
    		cutAccelerationList();
    		motionDetectFilter.enableDelayTime(false);
    		Log.i("MotionDetectFilter", "강제로 컷 ");
    	}
    	if (accelerationList.size() < dataLimit) {
    		Log.i("GestureController", "데이터 작음 : " + accelerationList.size());
    		fireRecognitionEvent(new RecognitionEvent(this, NOT_ENOUGH_DATA, null, null));
    		return;
    	}
    	GeneralHandler qHandler = new GeneralHandler();
    	QuantizerThread qThread = new QuantizerThread(qHandler, new ArrayList(accelerationList));
    	qThread.setDaemon(true);
    	qThread.run();
    	accelerationList = new ArrayList<AccelerationEvent>();
    }
    
    /**
     * 센서 정확도 변경시 호출되는 메소드 
     */
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
    
    long motionId = 1;
    /**
     * 센서값 변경시 호출되는 메소드 
     */
	public void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor;
		double[] values = new double[3];
		values[0] = event.values[0]; values[1] = event.values[1]; values[2] = event.values[2];
        
        if (this.isAccelerationEnabled && sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			for (int i = 0; i < 2; i++) {
				values = filters.get(i).filter(values);
				if (values == null)
					return;
			}
			this.fireAccelerationEvent(new AccelerationEvent(event, values[0], values[1], values[2], motionId++));
		}
	}
	
    /**
     * 모션 분석 모드 설정 
     * @param value 모션 분석 모드로 전환할지 말지에 대한 값 
     */
	public void enableRecognitionMode(boolean value) {
		isRecognitionMode = value;
	}

	/**
	 * 현재 모션 분석 모드인지 여부 
	 * @return 현재 모션 분석 모드이면 true를 리턴, 아니면 false를 리턴.
	 */
	public boolean isRecognitionMode() {
		return isRecognitionMode;
	}

	/**
	 * 현재 모션 분석 중인지 여부 
	 * @return 현재 모션 분석 중이면 true를 리턴, 아니면 false를 리턴. 
	 */
	public boolean isRecognizing() {
		return isRecognizing;
	}
	
	/**
	 * 모션 분석 중인지에 대한 값 설정 
	 * @param isRecognizing 부션 분석 중으로 전환하려면 true, 아니면 false
	 */
	public void setRecognizing(boolean isRecognizing) {
		this.isRecognizing = isRecognizing;
	}
	
	/**
	 * 학습 중인지 여부
	 * @return 학습 중이면 true를 리턴, 아니면 false를 리턴 
	 */
	public boolean isLearning() {
		return isLearning;
	}
	
	/**
	 * 현재까지 학습된 데이터를 제공 
	 * @return 현재까지 학습된 데이터 
	 */
	public List<List<Direction>> getLearningData() {
		return listOfAccList;
	}
	
	/**
	 * 특정 학습 데이터를 제거 
	 * @param idx 특정 학습 데이터를 가리키는 인덱스 
	 */
	public void removeLearningData(int idx) {
		listOfAccList.remove(idx);
	}
	
	/**
	 * 학습 데이터를 파일에 저장 
	 * @param fileName 파일명(모델명)
	 */
	public void saveLearningDataToFile(String fileName) {
		fileIO.writeObservationsSequencesToFile(fileName, listOfAccList);
		listOfAccList = new ArrayList<List<Direction>>();
	}
	/**
	 * 학습 데이터를 모델링 
	 * @param data 학습 데이터 
	 */
	public void trainLearningData(Map<String, List<List<Direction>>> data) {
		modelList = new HashMap<String, HMM>();
		GeneralHandler tHandler = new GeneralHandler();
    	TrainingThread tThread = new TrainingThread(tHandler, data, modelList);
    	tThread.run();
	}
	
	/**
	 * 현재까지 모델링된 모델들을 제공 
	 * @return 모델들 
	 */
	public Map<String, HMM> getLearnedModels() {
		return modelList;
	}
	/**
	 * 파일로부터 학습 데이터를 불러옴 
	 * @param modelNames 모델명 
	 * @return 모델의 학습 데이터 
	 */
	public Map<String, List<List<Direction>>> readLearningDataFromFiles(String[] modelNames) {
		return fileIO.readObservationsSequencesFromFiles(null);
	}
	/**
	 * 파일로부터 모델을 불러옴 
	 * @param modelNames 모델명 리스트 
	 * @return 모델들 
	 */
	public Map<String, Hmm<ObservationDiscrete<Direction>>> readModelsFromFiles(List<String> modelNames) {
		return fileIO.loadHMMData(modelNames);
	}
	/**
	 * 모델들을 모델 분석 클래스에 등록 
	 * @param data 모델 리스트 
	 */
	public void setModels(Map<String, Hmm<ObservationDiscrete<Direction>>> data) {
		analyzer = new Analyzer();
		for (String modelName : data.keySet()) {
			HMM model = new HMM(modelName);
			model.setHmm(data.get(modelName));
			analyzer.addModel(model);
		}
	}
	/**
	 * 가속도 센서 리스너 등록 
	 * @param listener 가속도 센서 리스너 
	 */
	public void addAccelerationListener(AccelerationListener listener) {
        listenerList.add(listener);
    }
	/**
	 * 가속도 센서 리스너 제거 
	 * @param listener 가속도 센서 리스너 
	 */
    public void removeAccelerationListener(AccelerationListener listener) {
       listenerList.remove(listener);
    }
    /**
     * 방향 센서 리스너 등록 
     * @param listener 방향 센서 리스너 
     */
    public void addOrientationListener(OrientationListener listener) {
        listenerList.add(listener);
    }
	/**
	 * 방향 센서 리스너 제거 
	 * @param listener 방향 센서 리스너 
	 */
    public void removeOrientationListener(OrientationListener listener) {
       listenerList.remove(listener);
    }
    /**
     * 학습 관련 리스너 등록
     * @param listener 학습 관련 리스너 
     */
    public void addLearningListener(LearningListener listener) {
        listenerList.add(listener);
    }
	/**
	 * 학습 관련 리스너 제거 
	 * @param listener 학습 관련 리스너 
	 */
    public void removeLearningListener(LearningListener listener) {
       listenerList.remove(listener);
    }
    /**
     * 모델링 관련 리스너 등록 
     * @param listener 모델링 관련 리스너 
     */
    public void addAnalysisListener(AnalysisListener listener) {
        listenerList.add(listener);
    }
	/**
	 * 모델링 관련 리스너 제거 
	 * @param listener 모델링 관련 리스너 
	 */
    public void removeAnalysisListener(AnalysisListener listener) {
       listenerList.remove(listener);
    }
    /**
     * 모션 분석 관련 리스너 등록 
     * @param listener 모션 분석 관련 리스너 
     */
    public void addRecognitionListener(RecognitionListener listener) {
        listenerList.add(listener);
    }
	/**
	 * 모션 분석 관련 리스너 제거 
	 * @param listener 모션 분석 관련 리스너 
	 */
    public void removeRecognitionListener(RecognitionListener listener) {
       listenerList.remove(listener);
    }
    /**
     * 모션 끝을 나타내는 인덱스 값 설정 
     */
    public void setCutIdx() {
		cutIdx = accelerationList.size();
	}
    /**
     * 모션 끝으로 설정된 부분부터 등록된 센서 값 제거 
     */
	public void cutAccelerationList() {
		if (cutIdx == -1) {
			Log.i("GestureController", "cut error");
			return;
		}
		accelerationList = accelerationList.subList(0, cutIdx);
		cutIdx = -1;
	}
	/**
	 * 양자화 및 학습, 모션 분석과 관련 쓰레드와 통신하는 핸들러 
	 * @author limjunsung
	 *
	 */
    class GeneralHandler extends Handler {
    	@Override
    	public void handleMessage(Message msg) {
    		switch(msg.what) {
    		case END_QUANTIZATION:
    			List<Direction> dirs = (List<Direction>)msg.obj;
    			if (isLearning) {
    				listOfAccList.add(dirs);
    				fireLearningEvent(new LearningEvent(this, END_LEARNING, dirs, null));
    				isLearning = false;
    			}
    			else if (isRecognitionMode) {
    				GeneralHandler mHandler = new GeneralHandler();
    		    	RecognitionThread rThread = new RecognitionThread(mHandler, analyzer, dirs);
    		    	rThread.setDaemon(true);
    		    	rThread.run();
    			}
    			break;
    		case SUCCESS_MODELING:
    			fireAnalysisEvent(new AnalysisEvent(this, GestureController.SUCCESS_MODELING, null, null));
    			break;
    		case END_MODELING:
    			fireAnalysisEvent(new AnalysisEvent(this, GestureController.END_MODELING, null, null));
    			break;
    		case END_RECOGNITION:
    			fireRecognitionEvent(new RecognitionEvent(this, GestureController.END_RECOGNITION, msg.obj, null));
    			break;
    		}
    	}
    }

}
/**
 * 양자화 클래스 
 * @author limjunsung
 *
 */
class QuantizerThread extends Thread {
	Handler mHandler;
	List<AccelerationEvent> accList;
	
	public QuantizerThread(Handler mHandler, List<AccelerationEvent> accList ) {
		this.mHandler = mHandler;
		this.accList = accList;
	}
	
	// 양자화 
	public void run() {
		double distanceX = 0, distanceZ = 0, velocityX = 0, velocityZ = 0, oriDistX = 0, oriDistZ = 0;
		List<Direction> dirList = new ArrayList<Direction>();
		for (AccelerationEvent acc : accList) {
			velocityX += acc.getX();
			velocityZ += acc.getZ();
			oriDistX = distanceX;
			oriDistZ = distanceZ;
			distanceX += velocityX;
			distanceZ += velocityZ;
			dirList.add(findDirection(distanceX - oriDistX, distanceZ - oriDistZ));
		}
		mHandler.sendMessage(mHandler.obtainMessage(GestureController.END_QUANTIZATION, dirList));
	}
	
	public Direction findDirection(double diffX, double diffY) {
		double th = Math.atan((double)diffY/(double)Math.abs(diffX));
		if (diffX < 0) {
			if (th < -Math.PI*3/8) {
				return Direction.SOUTH;
			} else if (th >= -Math.PI*3/8 && th < -Math.PI/8) {
				return Direction.SOUTH_EAST;
			} else if (th >= -Math.PI/8 && th < Math.PI/8) {
				return Direction.EAST;
			} else if (th >= Math.PI/8 && th < Math.PI*3/8) {
				return Direction.NORTH_EAST;
			} else if (th >= Math.PI*3/8) {
				return Direction.NORTH;
			}
		} else {
			if (th < -Math.PI*3/8) {
				return Direction.SOUTH;
			} else if (th >= -Math.PI*3/8 && th < -Math.PI/8) {
				return Direction.SOUTH_WEST;
			} else if (th >= -Math.PI/8 && th < Math.PI/8) {
				return Direction.WEST;
			} else if (th >= Math.PI/8 && th < Math.PI*3/8) {
				return Direction.NORTH_WEST;
			} else if (th >= Math.PI*3/8) {
				return Direction.NORTH;
			}
		}
		return null;
	}
}

/**
 * 학습 클래스 
 * @author limjunsung
 *
 */
class TrainingThread extends Thread {
	private Map<String, List<List<Direction>>> trainingData;
	private Handler mHandler;
	private Map<String, HMM> modelList;
	public TrainingThread(Handler mHandler, Map<String, List<List<Direction>>> data, Map<String, HMM> modelList) {
		this.trainingData = data;
		this.mHandler = mHandler;
		this.modelList = modelList;
	}
	
	public void run() {
		for (String modelName : trainingData.keySet()) {
			List<List<Direction>> data = trainingData.get(modelName);
			HMM model = new HMM(modelName);
			model.train(data);
			modelList.put(modelName, model);
			//analyzer.addModel(model);
			mHandler.sendMessage(mHandler.obtainMessage(GestureController.SUCCESS_MODELING));
		}
		mHandler.sendMessage(mHandler.obtainMessage(GestureController.END_MODELING));
	}
}
/**
 * 모션 분석 클래스 
 * @author limjunsung
 *
 */
class RecognitionThread extends Thread {
	private Analyzer analyzer;
	private List<Direction> data;
	private Handler mHandler;
	
	public RecognitionThread(Handler mHandler, Analyzer analyzer, List<Direction> data) {
		this.analyzer = analyzer;
		this.data = data;
		this.mHandler = mHandler;
	}
	
	public void run() {
		HMM hmm = analyzer.analyze(data);
		mHandler.sendMessage(mHandler.obtainMessage(GestureController.END_RECOGNITION, hmm));
	}
}
