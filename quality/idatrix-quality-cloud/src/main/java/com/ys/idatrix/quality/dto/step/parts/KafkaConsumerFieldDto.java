package com.ys.idatrix.quality.dto.step.parts;

/**
 * 
 * SPKafkaConsumerInput 的
 * org.pentaho.big.data.kettle.plugins.kafka.KafkaConsumerField
 *
 * @author XH
 * @since 2018年10月31日
 *
 */
public class KafkaConsumerFieldDto {

	private String kafkaName;
	private String outputName;
	private String outputType;

	public String getKafkaName() {
		return kafkaName;
	}

	public void setKafkaName(String kafkaName) {
		this.kafkaName = kafkaName;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

}
