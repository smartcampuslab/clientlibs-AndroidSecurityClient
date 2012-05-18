package eu.trentorise.smartcampus.ac;

public interface AuthListener {

	void onTokenAcquired(String token);
	
	void onAuthFailed(String error);
	
	void onAuthCancelled();
}

