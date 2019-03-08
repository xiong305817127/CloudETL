package com.ys.idatrix.metacube.metamanage.beans;

public class AmbariHostInfoBean {

    /**
     * href : http://ysbdh03.gdbd.com:8080/api/v1/clusters/sz/hosts/ysbdh03.gdbd.com?fields=Hosts/ip
     * Hosts : {"cluster_name":"sz","host_name":"ysbdh03.gdbd.com","ip":"10.0.0.83"}
     */

    private String href;
    private HostsBean Hosts;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public HostsBean getHosts() {
        return Hosts;
    }

    public void setHosts(HostsBean Hosts) {
        this.Hosts = Hosts;
    }

    public static class HostsBean {

        /**
         * cluster_name : sz host_name : ysbdh03.gdbd.com ip : 10.0.0.83
         */

        private String cluster_name;
        private String host_name;
        private String ip;

        public String getCluster_name() {
            return cluster_name;
        }

        public void setCluster_name(String cluster_name) {
            this.cluster_name = cluster_name;
        }

        public String getHost_name() {
            return host_name;
        }

        public void setHost_name(String host_name) {
            this.host_name = host_name;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }
}
