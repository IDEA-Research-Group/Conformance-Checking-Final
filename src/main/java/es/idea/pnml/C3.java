package es.idea.pnml;

import java.util.ArrayList;
import java.util.List;

public class C3 {
	protected List<String> inputTokens;
	protected boolean can;
	protected List<String> outputTokens;

	
	public C3(){
		inputTokens=new ArrayList<String>();
		can=false;
		outputTokens=new ArrayList<String>();
	}


	public List<String> getInputTokens() {
		return inputTokens;
	}


	public void setInputTokens(List<String> inputTokens) {
		this.inputTokens = inputTokens;
	}


	public boolean isCan() {
		return can;
	}


	public void setCan(boolean can) {
		this.can = can;
	}


	public List<String> getOutputTokens() {
		return outputTokens;
	}


	public void setOutputTokens(List<String> outputTokens) {
		this.outputTokens = outputTokens;
	}


}
