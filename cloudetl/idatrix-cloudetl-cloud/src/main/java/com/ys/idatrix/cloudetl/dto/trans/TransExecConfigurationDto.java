/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.trans;

/**
 * 转换执行配置
 * @author JW
 * @since 05-12-2017
 *
 */
public class TransExecConfigurationDto {
	
    private boolean execLocal;
    private boolean execRemote;
    private String remoteServer;
    private boolean passExport;
    private boolean execCluster;
    private boolean clusterPrepare;
    private boolean clusterPost;
    private boolean clusterStart;
    private boolean clusterShowTrans;
    private boolean safeMode;
    private boolean gatherMetrics;
    private String logLevel;
    
    public void setExecLocal(boolean execLocal) {
        this.execLocal = execLocal;
    }
    public boolean getExecLocal() {
        return execLocal;
    }

    public void setExecRemote(boolean execRemote) {
        this.execRemote = execRemote;
    }
    public boolean getExecRemote() {
        return execRemote;
    }

    public void setRemoteServer(String remoteServer) {
        this.remoteServer = remoteServer;
    }
    public String getRemoteServer() {
        return remoteServer;
    }

    public void setPassExport(boolean passExport) {
        this.passExport = passExport;
    }
    public boolean getPassExport() {
        return passExport;
    }

    public void setExecCluster(boolean execCluster) {
        this.execCluster = execCluster;
    }
    public boolean getExecCluster() {
        return execCluster;
    }

    public void setClusterPrepare(boolean clusterPrepare) {
        this.clusterPrepare = clusterPrepare;
    }
    public boolean getClusterPrepare() {
        return clusterPrepare;
    }

    public void setClusterPost(boolean clusterPost) {
        this.clusterPost = clusterPost;
    }
    public boolean getClusterPost() {
        return clusterPost;
    }

    public void setClusterStart(boolean clusterStart) {
        this.clusterStart = clusterStart;
    }
    public boolean getClusterStart() {
        return clusterStart;
    }

    public void setClusterShowTrans(boolean clusterShowTrans) {
        this.clusterShowTrans = clusterShowTrans;
    }
    public boolean getClusterShowTrans() {
        return clusterShowTrans;
    }

    public void setSafeMode(boolean safeMode) {
        this.safeMode = safeMode;
    }
    public boolean getSafeMode() {
        return safeMode;
    }

    public void setGatherMetrics(boolean gatherMetrics) {
        this.gatherMetrics = gatherMetrics;
    }
    public boolean getGatherMetrics() {
        return gatherMetrics;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
    public String getLogLevel() {
        return logLevel;
    }
    
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransExecConfigurationDto [execLocal=" + execLocal + ", execRemote=" + execRemote + ", remoteServer="
				+ remoteServer + ", passExport=" + passExport + ", execCluster=" + execCluster + ", clusterPrepare="
				+ clusterPrepare + ", clusterPost=" + clusterPost + ", clusterStart=" + clusterStart
				+ ", clusterShowTrans=" + clusterShowTrans + ", safeMode=" + safeMode + ", gatherMetrics="
				+ gatherMetrics + ", logLevel=" + logLevel + "]";
	}

}
