package com.ys.idatrix.metacube.metamanage.beans;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Host {

    private String hostname;
    private String ip;
    private String port;
}
