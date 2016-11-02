package se.inera.statistikapi.takapi;

public class ConnectionPoint {
	
	private int id;
    private String platform;
    private String environment;
    private String snapshotTime;
    
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getPlatform() {
		return platform;
	}
	
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	public String getEnvironment() {
		return environment;
	}
	
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	
	public String getSnapshotTime() {
		return snapshotTime;
	}
	
	public void setSnapshotTime(String snapshotTime) {
		this.snapshotTime = snapshotTime;
	}
}
