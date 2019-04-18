package com.ys.test.ivy;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


/**
 * 云化数据集成系统 iDatrix CloudETL
 */

/**
 *
 * @author JW
 * @since 
 *
 */
public class testIvyMain {
	
	
	private static String pomRoot="D:\\tool\\eclipse\\workspace\\ys-idatrix-cloud-etl\\";
	private static String ivyRoot="D:\\tool\\eclipse\\workspace\\pentaho-kettle-master\\";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// default system  plugins
		printIvyToPom("plugins");
	}

	public static void printIvyToPom(String type) {

		try {
			
			String root = ivyRoot;
			String[] paths = new String[] { "ivy.xml", "engine\\ivy.xml", "core\\ivy.xml", "dbdialog\\ivy.xml","ui\\ivy.xml" , "assembly\\ivy.xml"};
			List<Document> docs = Lists.newArrayList();
			for (String p : paths) {
					String path = root + p;
					Document doc = XMLHandler.loadXMLFile(path);
					docs.add(doc);
			}

			StringBuffer dependency = new StringBuffer();
			Map<String,String>  dependencySet = printPomToPom();
			Set<String> property= Sets.newHashSet();
			
			for (Document doc : docs) {

				String defaultOrganisation = doc.getElementsByTagName("info").item(0).getAttributes().getNamedItem("organisation").getNodeValue();
				String defaultModule = doc.getElementsByTagName("info").item(0).getAttributes().getNamedItem("module").getNodeValue();
				String defaultVision = doc.getElementsByTagName("info").item(0).getAttributes().getNamedItem("revision").getNodeValue();

				NodeList dependencies = doc.getElementsByTagName("dependency");
				for (int i = 0; i < dependencies.getLength(); i++) {
					NamedNodeMap attrs = dependencies.item(i).getAttributes();

					String conf = attrs.getNamedItem("conf")!=null?attrs.getNamedItem("conf").getNodeValue():null;
					if( type ==null || "".equals(type) || "default".equals(type) ){
						if(conf != null && !conf.equals("")&&!conf.equals("default->default")){
							continue ;
						}
						
					}else if("system".equals(type)){
						if(!"pentaho-system->default".equals(conf)){
							continue ;
						}
					}else if("plugins".equals(type)){
						if(!"plugins->default".equals(conf)){
							continue ;
						}
					}
					
					
					String org = attrs.getNamedItem("org").getNodeValue();
					String name = attrs.getNamedItem("name").getNodeValue();
					String rev = attrs.getNamedItem("rev").getNodeValue();
					
					org = StringUtils.isEmpty(org)? defaultOrganisation:org.trim();
					name = StringUtils.isEmpty(name)? defaultModule:name.trim();
					rev = StringUtils.isEmpty(rev)? defaultVision:rev.trim();
					
					if(org.equals("${ivy.artifact.group}")){
						org="pentaho-kettle";
					}
					if(org.equals("${dependency.reporting-engine.group}")){
						org="org.pentaho.reporting.engine";
					}
					
					if(org.startsWith("${")){
						property.add(org);
					}
					if(name.startsWith("${")){
						property.add(name);
					}
					if(rev.startsWith("${")){
						property.add(rev);
					}

					if(!dependencySet.keySet().contains(org+"-"+name) ){
						dependencySet.put(org+"-"+name,rev);
						dependency.append("<dependency>").append("\n");
						dependency.append("	<groupId>" + org + "</groupId>").append("\n");;
						dependency.append("	<artifactId>" + name + "</artifactId>").append("\n");;
						dependency.append("	<version>" + rev + "</version>").append("\n");
					}else{
						if(!rev.equals(dependencySet.get(org+"-"+name))){
							System.out.println(org+"-"+name +"  已有版本:"+dependencySet.get(org+"-"+name) +" 新版本:"+rev);
						}
						continue;
					}
					

					boolean ifExcusions = false;
					NodeList childExcus = dependencies.item(i).getChildNodes();
					for (int j = 0; j < childExcus.getLength(); j++) {
						if (childExcus.item(j).getNodeName().equals("exclude")) {

							NamedNodeMap iattrs = childExcus.item(j).getAttributes();
							if (iattrs != null && iattrs.getLength() > 0) {
								Node iorg = iattrs.getNamedItem("org");
								String iorgv = defaultOrganisation;
								if (iorg != null) {
									iorgv = iorg.getNodeValue();
								}
								
								if(iorgv.equals("${ivy.artifact.group}")){
									iorgv="pentaho-kettle";
								}
								if(iorgv.equals("${dependency.reporting-engine.group}")){
									iorgv="org.pentaho.reporting.engine";
								}
								
								Node iname = iattrs.getNamedItem("name");
								String inamev = defaultModule;
								if (iname != null) {
									inamev = iname.getNodeValue();
								}

								if (iorgv != null && inamev != null) {
									if (!ifExcusions) {
										dependency.append("	<exclusions>").append("\n");
										ifExcusions = true;
									}

									dependency.append("		<exclusion>").append("\n");
									dependency.append("			<groupId>" + iorgv + "</groupId>").append("\n");
									dependency.append("			<artifactId>" + inamev + "</artifactId>").append("\n");
									dependency.append("		</exclusion>").append("\n");

								}

							}

						}

					}
					if (ifExcusions) {
						dependency.append("	</exclusions>").append("\n");
						ifExcusions = false;
					}

					dependency.append("</dependency>").append("\n");

				}

			}
			System.out.println("\n\n\n");
			System.out.println("<properties>");
			for(String p:property){
				System.out.println("<"+p.replaceAll("\\$\\{", "").replaceAll("\\}", "")+">"+"    "+"</"+p.replaceAll("\\$\\{", "").replaceAll("\\}", "")+">");
			}
			System.out.println("</properties>");
			System.out.println();
			System.out.println(dependency.toString());
		
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static Map<String,String> printPomToPom(){
		
		String root = pomRoot;
		
		String path="pom.xml";
		
		try {
			Document doc = XMLHandler.loadXMLFile(root+path);
			
			NodeList dependencies = doc.getElementsByTagName("dependency");
			Map<String,String> dependencySet = Maps.newHashMap();
			for (int i = 0; i < dependencies.getLength(); i++) {
				 NodeList childNode = dependencies.item(i).getChildNodes();
				 String groupId = null;
				String artifactId = null;
				String version = null;
				for( int j=0;j<childNode.getLength();j++){
					 Node node = childNode.item(j);
					 if(node.getNodeName().equals("groupId")){
						 groupId=node.getTextContent();
					 }
					 if(node.getNodeName().equals("artifactId")){
						 artifactId=node.getTextContent();
					 }
					 if(node.getNodeName().equals("version")){
						  version = node.getTextContent();
					 }
				 }
				 dependencySet.put(groupId+"-"+artifactId,version);
			}
			
			return dependencySet;
			
		} catch (KettleXMLException e) {
			e.printStackTrace();
		}
		
		return null;
		
		
	}
	
	
}
