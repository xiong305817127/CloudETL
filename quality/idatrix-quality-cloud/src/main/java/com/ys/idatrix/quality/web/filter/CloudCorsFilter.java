/**
 * 云化数据集成系统
 * iDatrix quality
 */
package com.ys.idatrix.quality.web.filter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


public class CloudCorsFilter extends CorsFilter {

	public CloudCorsFilter() {
		super(getCorsConfigurationSource());
	}
	
	
	private static CorsConfigurationSource getCorsConfigurationSource() {
		UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
		
		CorsConfiguration config =  new CorsConfiguration();
		config.addAllowedOrigin("*");
		config.addAllowedMethod("*");
		config.setAllowCredentials(true);
		config.addAllowedHeader("*");
		//config.addAllowedHeader("VT");
		//config.addAllowedHeader("Content-Type");
		corsConfigurationSource.registerCorsConfiguration("/**", config);
		
		return corsConfigurationSource;
	}
}
