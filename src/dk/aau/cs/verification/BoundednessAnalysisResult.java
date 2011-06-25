package dk.aau.cs.verification;

public class BoundednessAnalysisResult {
	private int maxUsedTokens;
	private int totalTokens;
	private int extraTokens;
	
	public BoundednessAnalysisResult(int totalTokens, int maxUsedTokens, int extraTokens){
		this.maxUsedTokens = maxUsedTokens;
		this.totalTokens = totalTokens;
		this.extraTokens = extraTokens;
	}
	
	@Override
	public String toString() {
		if(boundednessResult().equals(Boundedness.Bounded)){
			return "";
		}else{
			StringBuffer buffer = new StringBuffer();
			buffer.append(System.getProperty("line.separator"));
			buffer.append(System.getProperty("line.separator"));
			buffer.append(String.format("Only markings with at most %1$d tokens (%2$d extra tokens)", totalTokens, extraTokens));
			buffer.append(System.getProperty("line.separator"));
			buffer.append("were explored. Try to increase the number of extra tokens.");
			return buffer.toString();
		}
	}

	public Boundedness boundednessResult(){
		if(maxUsedTokens <= totalTokens) return Boundedness.Bounded;
		else return Boundedness.NotBounded;
	}
	
	public int usedTokens() {
		return maxUsedTokens;
	}
}
