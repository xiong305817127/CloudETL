package com.ys.idatrix.cloudetl.steps;

import java.util.Collections;
import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.replacestring.ReplaceStringMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

@Step(id = "Desensitization", image = "Desensitization.svg", name = "Desensitization", description = "Desensitization  Description", categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Transform", documentationUrl = "", i18nPackageName = "")
public class DesensitizationMeta extends ReplaceStringMeta implements StepMetaInterface {

	private static Class<?> PKG = ReplaceStringMeta.class;

	// 有效,继承父类
	// private String[] fieldInStream;
	// private String[] fieldOutStream;

	private String[] ruleTypes; // mask(掩码) , truncation(截断)
	private int[] startPositons; // 替换开始位置 ,0为第一位, -1为倒数第一位(反向替换),-2为倒数第二位(反向替换) ,...
	private int[] lengths; // 替换的长度 , 0为不替换,-1为替换到最后一位
	private String[] replacements; // 替换的字符串, [掩码]为* , [截断]时设置为空(相当于替换为空)
	private Boolean[] ignoreSpaces; // 替换时忽略空格

	public void ruleTransformation() {
		int nrkeys = ruleTypes.length;
		for (int i = 0; i < nrkeys; i++) {
			String ruleType = Utils.isEmpty(ruleTypes[i]) ? "mask" : ruleTypes[i];
			int startPositon = startPositons[i];
			int length = lengths[i];
			String replacement = Utils.isEmpty(replacements[i]) ? "*" : replacements[i];
			Boolean ignoreSpace = ignoreSpaces[i] == null ? true : ignoreSpaces[i];

			String lengthStr =  "";
			if( length == 0 ) {
				//不替换
				continue ;
			}else if( length < 0 ){
				//替换所有
				lengthStr="*" ;
			}else {
				//替换指定长度
				lengthStr="{"+length+"}" ;
			}
			
			StringBuffer pattern = new StringBuffer(); // "\\S{4}\\s*$" 替换最后四位非空字符
			if (startPositon == 0) {
				// 替换开头 N 位,^\s*\S{3}
				pattern.append("^").append(ignoreSpace ? "\\s*" : "").append("\\S").append(lengthStr);
			} else if (startPositon == -1) {
				// 替换最后 N 位, \S{3}\s*$
				pattern.append("\\S").append(lengthStr).append(ignoreSpace ? "\\s*" : "").append("$");
			} else if (startPositon > 0) {
				// 替换从前面startPositon开始算 N 位, 正后发断言 (?<=^\S{3})\S{3}  ,  append(ignoreSpace ? "\\s*" : "") 会造成报错,暂时忽略
				pattern.append("(?<=^").append("\\S{").append(startPositon) .append("})").append("\\S").append(lengthStr);
			} else if (startPositon < -1) {
				// 替换从最后startPositon开始算 N 位,正先行断言 \S{3}(?=\S{3}$)
				pattern.append("\\S").append(lengthStr).append("(?=\\S{").append((-1 - startPositon)).append("}") .append(ignoreSpace ? "\\s*" : "").append("$)");
			}
			getReplaceString()[i] = pattern.toString();

			if ("truncation".equals(ruleType)) {
				// 截断
				isSetEmptyString()[i] = true;
				getReplaceByString()[i] = "";
			} else {
				// 掩码
				isSetEmptyString()[i] = false;
				getReplaceByString()[i] = String.join("", Collections.nCopies( ( length<=0?3:length), replacement));
			}

			getUseRegEx()[i] = 1; // 使用正则表达式
			getWholeWord()[i] = 0; // 非全词匹配
			getCaseSensitive()[i] = 0; // 大小写不敏感

		}

	}

	@Override
	public void allocate(int nrkeys) {
		super.allocate(nrkeys);
		ruleTypes = new String[nrkeys];
		startPositons = new int[nrkeys];
		lengths = new int[nrkeys];
		replacements = new String[nrkeys];
		ignoreSpaces = new Boolean[nrkeys];

	}

	@Override
	public Object clone() {
		DesensitizationMeta retval = (DesensitizationMeta) super.clone();
		int nrkeys = ruleTypes.length;

		System.arraycopy(ruleTypes, 0, retval.ruleTypes, 0, nrkeys);
		System.arraycopy(startPositons, 0, retval.startPositons, 0, nrkeys);
		System.arraycopy(lengths, 0, retval.lengths, 0, nrkeys);
		System.arraycopy(replacements, 0, retval.replacements, 0, nrkeys);
		System.arraycopy(ignoreSpaces, 0, retval.ignoreSpaces, 0, nrkeys);

		return retval;

	}

	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {

		// 没有必要加载
		// super.loadXML(stepnode, databases, metaStore);
		try {

			Node lookup = XMLHandler.getSubNode(stepnode, "desensitizations");
			int nrkeys = XMLHandler.countNodes(lookup, "desensitization");

			allocate(nrkeys);

			for (int i = 0; i < nrkeys; i++) {
				Node fnode = XMLHandler.getSubNodeByNr(lookup, "desensitization", i);

				getFieldInStream()[i] = Const.NVL(XMLHandler.getTagValue(fnode, "inStreamName"), "");
				getFieldOutStream()[i] = Const.NVL(XMLHandler.getTagValue(fnode, "outStreamName"), "");

				ruleTypes[i] = Const.NVL(XMLHandler.getTagValue(fnode, "ruleType"), "");
				startPositons[i] = Integer.valueOf(Const.NVL(XMLHandler.getTagValue(fnode, "startPositon"), "0"));
				lengths[i] = Integer.valueOf(Const.NVL(XMLHandler.getTagValue(fnode, "length"), "0"));
				replacements[i] = Const.NVL(XMLHandler.getTagValue(fnode, "replacement"), "*");
				ignoreSpaces[i] = "Y".equalsIgnoreCase(Const.NVL(XMLHandler.getTagValue(fnode, "ignoreSpace"), "Y"));

			}

			ruleTransformation();

		} catch (Exception e) {
			throw new KettleXMLException(
					BaseMessages.getString(PKG, "ReplaceStringMeta.Exception.UnableToReadStepInfoFromXML"), e);
		}
	}

	@Override
	public String getXML() {
		StringBuilder retval = new StringBuilder(500);

		retval.append("    <desensitizations>").append(Const.CR);

		for (int i = 0; i < getFieldInStream().length; i++) {
			retval.append("      <desensitization>").append(Const.CR);
			retval.append("        ").append(XMLHandler.addTagValue("inStreamName", getFieldInStream()[i]));
			retval.append("        ").append(XMLHandler.addTagValue("outStreamName", getFieldOutStream()[i]));
			retval.append("        ").append(XMLHandler.addTagValue("ruleType", ruleTypes[i]));
			retval.append("        ").append(XMLHandler.addTagValue("startPositon", startPositons[i]));
			retval.append("        ").append(XMLHandler.addTagValue("length", lengths[i]));
			retval.append("        ").append(XMLHandler.addTagValue("replacement", replacements[i]));
			retval.append("        ").append(XMLHandler.addTagValue("ignoreSpace", ignoreSpaces[i]));
			retval.append("      </desensitization>").append(Const.CR);
		}
		retval.append("    </desensitizations>").append(Const.CR);

		return retval.toString();
	}

	@Override
	public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases)
			throws KettleException {
		try {
			int nrkeys = rep.countNrStepAttributes(id_step, "inStreamName");

			allocate(nrkeys);
			for (int i = 0; i < nrkeys; i++) {
				getFieldInStream()[i] = Const.NVL(rep.getStepAttributeString(id_step, i, "inStreamName"), "");
				getFieldOutStream()[i] = Const.NVL(rep.getStepAttributeString(id_step, i, "outStreamName"), "");
				ruleTypes[i] = Const.NVL(rep.getStepAttributeString(id_step, i, "ruleType"), "");
				startPositons[i] = Integer.valueOf( Const.NVL(rep.getStepAttributeString(id_step, i, "startPositon") ,"0") );
				lengths[i] =   Integer.valueOf( Const.NVL(rep.getStepAttributeString(id_step, i, "length" ),"0") );
				replacements[i] = Const.NVL(rep.getStepAttributeString(id_step, i, "replacement"), "");
				ignoreSpaces[i] = rep.getStepAttributeBoolean(id_step, i, "ignoreSpace") ;

			}
			
			ruleTransformation();
			
		} catch (Exception e) {
			throw new KettleException(
					BaseMessages.getString(PKG, "ReplaceStringMeta.Exception.UnexpectedErrorInReadingStepInfo"), e);
		}
	}

	@Override
	public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step)
			throws KettleException {
		try {
			for (int i = 0; i < getFieldInStream().length; i++) {
				rep.saveStepAttribute(id_transformation, id_step, i, "inStreamName", getFieldInStream()[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "outStreamName", getFieldOutStream()[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "ruleType", ruleTypes[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "startPositon", startPositons[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "length", lengths[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "replacement", replacements[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "ignoreSpace", ignoreSpaces[i]);
			}
		} catch (Exception e) {
			throw new KettleException(
					BaseMessages.getString(PKG, "ReplaceStringMeta.Exception.UnableToSaveStepInfo") + id_step, e);
		}
	}

	public String[] getRuleTypes() {
		return ruleTypes;
	}

	public void setRuleTypes(String[] ruleTypes) {
		this.ruleTypes = ruleTypes;
	}

	public int[] getStartPositons() {
		return startPositons;
	}

	public void setStartPositons(int[] startPositons) {
		this.startPositons = startPositons;
	}

	public int[] getLengths() {
		return lengths;
	}

	public void setLengths(int[] lengths) {
		this.lengths = lengths;
	}

	public String[] getReplacements() {
		return replacements;
	}

	public void setReplacements(String[] replacements) {
		this.replacements = replacements;
	}

	public Boolean[] getIgnoreSpaces() {
		return ignoreSpaces;
	}

	public void setIgnoreSpaces(Boolean[] ignoreSpaces) {
		this.ignoreSpaces = ignoreSpaces;
	}

}
