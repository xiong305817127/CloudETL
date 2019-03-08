package com.ys.idatrix.cloudetl.ext.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.steps.webservices.WebServiceMeta;
import org.pentaho.di.trans.steps.webservices.wsdl.ComplexType;
import org.pentaho.di.trans.steps.webservices.wsdl.Wsdl;
import org.pentaho.di.trans.steps.webservices.wsdl.WsdlOpParameter;
import org.pentaho.di.trans.steps.webservices.wsdl.WsdlOpParameterContainer;
import org.pentaho.di.trans.steps.webservices.wsdl.WsdlOperation;
import org.pentaho.di.trans.steps.webservices.wsdl.WsdlOperationContainer;
import org.pentaho.di.trans.steps.webservices.wsdl.WsdlParamContainer;
import org.pentaho.di.trans.steps.webservices.wsdl.WsdlOpParameter.ParameterMode;

import com.ys.idatrix.cloudetl.dto.step.parts.WebServiceFieldDto;
import com.ys.idatrix.cloudetl.dto.step.parts.WebServiceOpDto;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.repository.CloudRepository;

/**
 * WebServiceUtils.java
 * 
 * @author FBZ
 * @since 2017年12月11日
 *
 */
public class WebServiceUtils {
	private static Class<?> PKG = WebServiceMeta.class;

	public static List<WebServiceOpDto> getOperations(String owner , String transName, String group ,String anURI, String user, String password)
			throws Exception {
		TransMeta transMeta = CloudRepository.loadTransByName(owner ,transName,group);

		Wsdl wsdl = WebServiceUtils.loadWebService(transMeta, anURI, user, password);

		List<WebServiceOpDto> rs = new ArrayList<>();

		if (wsdl != null) {
			List<WsdlOperation> listeOperations = wsdl.getOperations();
			Collections.sort(listeOperations, new Comparator<WsdlOperation>() {
				public int compare(WsdlOperation op1, WsdlOperation op2) {
					return op1.getOperationQName().getLocalPart().compareTo(op2.getOperationQName().getLocalPart());
				}
			});

			WebServiceFieldDto[] emptyWsDto = new WebServiceFieldDto[0];
			WebServiceOpDto opDto;
			WebServiceFieldDto dto;
			for (Iterator<WsdlOperation> itr = listeOperations.iterator(); itr.hasNext();) {
				WsdlOperation op = itr.next();
				opDto = new WebServiceOpDto();
				opDto.setMethod(op.getOperationQName().getLocalPart());
				rs.add(opDto);

				Object[] wdlsOp = WebServiceUtils.loadOperation(op, wsdl);
				WsdlParamContainer inWsdlParamContainer = (WsdlParamContainer) wdlsOp[1];
				WsdlParamContainer outWsdlParamContainer = (WsdlParamContainer) wdlsOp[2];

				if (inWsdlParamContainer != null) {
					String[] params = inWsdlParamContainer.getParamNames();
					opDto.setIn(new WebServiceFieldDto[params.length]);
					for (int i = 0; i < params.length; i++) {
						opDto.getIn()[i] = dto = new WebServiceFieldDto();
						dto.setName("");
						dto.setWsName(params[i]);
						dto.setType(inWsdlParamContainer.getParamType(params[i]));
					}
				} else {
					opDto.setIn(emptyWsDto);
				}

				String outContainerName = outWsdlParamContainer == null ? "out"
						: outWsdlParamContainer.getContainerName();

				opDto.setOutContainerName(null == outContainerName ? "out" : outContainerName);

				if (outWsdlParamContainer != null) {
					String[] outParams = outWsdlParamContainer.getParamNames();
					opDto.setOut(new WebServiceFieldDto[outParams.length]);
					for (int i = 0; i < outParams.length; i++) {
						opDto.getOut()[i] = dto = new WebServiceFieldDto();
						dto.setName(outParams[i]);
						dto.setWsName(outParams[i]);
						dto.setType(outWsdlParamContainer.getParamType(outParams[i]));
					}
				} else {
					opDto.setOut(emptyWsDto);
				}
			}
		}
		return rs;
	}

	public static Wsdl loadWebService(TransMeta transMeta, String url, String user, String password) {
		String anURI = transMeta.environmentSubstitute(url);
		Wsdl wsdl = null;
		try {
			wsdl = new Wsdl(new URI(anURI), null, null, user, password);
		} catch (Exception e) {
			wsdl = null;
			LogChannel.GENERAL.logError(BaseMessages.getString(PKG, "WebServiceDialog.ErrorDialog.Title") + anURI,
					CloudLogger.getExceptionMessage(e));
		}
		return wsdl;
	}

	public static Object[] loadOperation(WsdlOperation wsdlOperation, Wsdl wsdl) throws KettleException {
		WsdlParamContainer inWsdlParamContainer = null;
		WsdlParamContainer outWsdlParamContainer = null;

		if (wsdlOperation != null) {
			for (int cpt = 0; cpt < wsdlOperation.getParameters().size(); cpt++) {
				WsdlOpParameter param = wsdlOperation.getParameters().get(cpt);
				if (param.isArray()) {
					// setInFieldArgumentName(param.getName().getLocalPart());
					if (param.getItemXmlType() != null) {
						ComplexType type = param.getItemComplexType();
						if (type != null) {
							for (Iterator<String> itrType = type.getElementNames().iterator(); itrType.hasNext();) {
								String attributeName = itrType.next();
								QName attributeType = type.getElementType(attributeName);
								if (!WebServiceMeta.XSD_NS_URI.equals(attributeType.getNamespaceURI())) {
									throw new KettleStepException(BaseMessages.getString(PKG,
											"WebServiceDialog.ERROR0007.UnsupporteOperation.ComplexType"));
								}
							}
						}
						if (ParameterMode.IN.equals(param.getMode()) || ParameterMode.INOUT.equals(param.getMode())
								|| ParameterMode.UNDEFINED.equals(param.getMode())) {
							if (inWsdlParamContainer != null) {
								throw new KettleStepException(BaseMessages.getString(PKG,
										"WebServiceDialog.ERROR0006.UnsupportedOperation.MultipleArrays"));
							} else {
								inWsdlParamContainer = new WsdlOpParameterContainer(param);
							}
						} else if (ParameterMode.OUT.equals(param.getMode())
								|| ParameterMode.INOUT.equals(param.getMode())
								|| ParameterMode.UNDEFINED.equals(param.getMode())) {
							if (outWsdlParamContainer != null) {
								throw new KettleStepException(BaseMessages.getString(PKG,
										"WebServiceDialog.ERROR0006.UnsupportedOperation.MultipleArrays"));
							} else {
								outWsdlParamContainer = new WsdlOpParameterContainer(param);
							}
						}
					}
				} else {
					if (ParameterMode.IN.equals(param.getMode()) || ParameterMode.INOUT.equals(param.getMode())
							|| ParameterMode.UNDEFINED.equals(param.getMode())) {
						if (inWsdlParamContainer != null && !(inWsdlParamContainer instanceof WsdlOperationContainer)) {
							throw new KettleStepException(BaseMessages.getString(PKG,
									"WebServiceDialog.ERROR0008.UnsupportedOperation.IncorrectParams"));
						} else {
							inWsdlParamContainer = new WsdlOperationContainer(wsdlOperation, param.getMode());
						}
					} else if (ParameterMode.OUT.equals(param.getMode()) || ParameterMode.INOUT.equals(param.getMode())
							|| ParameterMode.UNDEFINED.equals(param.getMode())) {
						if (outWsdlParamContainer != null
								&& !(outWsdlParamContainer instanceof WsdlOperationContainer)) {
							throw new KettleStepException(BaseMessages.getString(PKG,
									"WebServiceDialog.ERROR0008.UnsupportedOperation.IncorrectParams"));
						} else {
							outWsdlParamContainer = new WsdlOperationContainer(wsdlOperation, param.getMode());
						}
					} else {
						LogChannel.GENERAL.logBasic("Parameter : " + param.getName().getLocalPart() + ", mode="
								+ param.getMode().toString() + ", is not considered");
					}
				}
			}
			if (wsdlOperation.getReturnType() != null) {
				outWsdlParamContainer = new WsdlOpParameterContainer((WsdlOpParameter) wsdlOperation.getReturnType());
//				if (wsdlOperation.getReturnType().isArray()) {
//					if (wsdlOperation.getReturnType().getItemXmlType() != null) {
//						ComplexType type = wsdlOperation.getReturnType().getItemComplexType();
//						if (type != null) {
//							for (Iterator<String> itrType = type.getElementNames().iterator(); itrType.hasNext();) {
//								String attributeName = itrType.next();
//								QName attributeType = type.getElementType(attributeName);
//								if (!WebServiceMeta.XSD_NS_URI.equals(attributeType.getNamespaceURI())) {
//									throw new KettleStepException(BaseMessages.getString(PKG,
//											"WebServiceDialog.ERROR0007.UnsupportedOperation.ComplexType"));
//								}
//							}
//						}
//					}
//				}
			}
		}

		Object[] r = new Object[3];
		r[0] = wsdlOperation;
		r[1] = inWsdlParamContainer;
		r[2] = outWsdlParamContainer;

		return r;
	}

	public static Object[] loadOperation(String anOperationName, Wsdl wsdl) throws KettleException {
		WsdlOperation wsdlOperation = null;

		if (wsdl != null) {
			for (Iterator<WsdlOperation> vItOperation = wsdl.getOperations().iterator(); vItOperation.hasNext()
					&& wsdlOperation == null;) {
				WsdlOperation vCurrentOperation = vItOperation.next();
				if (vCurrentOperation.getOperationQName().getLocalPart().equals(anOperationName)) {
					wsdlOperation = vCurrentOperation;
				}
			}
		}

		return WebServiceUtils.loadOperation(wsdlOperation, wsdl);
	}
}
