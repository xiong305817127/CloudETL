/**
 * 任务帮助页
 */
import React from 'react';
import { Button } from 'antd';

import { downloadFile } from '../../../utils/utils';
import Style from './TaskHelp.css';

const demoSpecification = {
  spark: {
    title: 'SparkSubmit',
    description: '主要提供Spark任务服务',
    demoFile: '',
    jarFile: 'files/task/jar/azkaban-job-spark-1.0-SNAPSHOT.jar',
    diagram: require('../../../assets/images/task/spark.png'),
    params: [
      ['类型', 'Spark拖选后节点内容 不可修改'],
      ['名称', '任务节点名称，任务名称不可重名'],
      ['Jar', '点击浏览上传Jar'],
      ['Class', '程序执行入口主函数'],
      ['Args', '需要传入到Main函数的参数'],
    ],
  },
  hadoop: {
    title: 'HadoopJava',
    description: '主要提供Hadoop任务服务',
    demoFile: 'files/task/demo/Template-HadoopJava.zip',
    jarFile: 'files/task/jar/wordcount-0.0.1-SNAPSHOT.jar',
    diagram: require('../../../assets/images/task/hadoop.png'),
    params: [
      ['类型', 'HadoopJava拖选后任务节点内容 不可修改'],
      ['名称', '任务节点名称，任务名称不可重名'],
      ['Jar', '点击浏览上传Jar'],
      ['Class', '程序执行入口主函数'],
      ['添加参数', (<div>
        <p>用户可以根据软件需要配置参数名，此处配置选项作为用户传参。用户在此处配置好参数值，在程序中可以获取</p>
        <p>详细见代码示例：</p>
        <table style={{borderCollapse:'collapse', width: '100%'}}>
          <thead>
            <tr>
              <th style={{width: 145}}>参数</th>
              <th>说明</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Output.path</td>
              <td>输出路径</td>
            </tr>
            <tr>
              <td>Input.path</td>
              <td>数据输入路径</td>
            </tr>
          </tbody>
        </table>
      </div>)],
    ],
  },
  command: {
    title: 'Command',
    description: '主要提供linux命令任务服务',
    demoFile: '',
    jarFile: '',
    diagram: require('../../../assets/images/task/command.png'),
    params: [
      ['类型', 'Command拖选后任务节点内容不可修改'],
      ['名称', '任务节点名称，任务名称不可重名'],
      ['命令', (<div>
        <p>点击添加行，添加需要执行的linux命令。此处添加两条命令：</p>
        <table style={{borderCollapse:'collapse', width: '100%'}}>
          <thead>
            <tr>
              <th>命令</th>
              <th>说明</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Echo this is command 01</td>
              <td>打印数据</td>
            </tr>
            <tr>
              <td>Echo this is command 02</td>
              <td>打印数据</td>
            </tr>
          </tbody>
        </table>
      </div>)]
    ],
  },
  hive: {
    title: 'HiveJob',
    description: '主要提供HiveJob任务服务',
    demoFile: '',
    jarFile: '',
    diagram: require('../../../assets/images/task/hive.png'),
    params: [
      ['类型', 'HiveJob拖选后任务节点内容不可修改'],
      ['名称', '任务节点名称，任务名称不可重名'],
      ['命令', (<div>
        <p>点击添加行，增加需要执行的HQL。此处完整HQL段为：</p>
        <table style={{borderCollapse:'collapse', width: '100%'}}>
          <tbody>
            <tr>
              <td>drop table words;</td>
            </tr>
            <tr>
              <td>create table words (freq int, word string) row format delimited fields terminated by '\t' stored as textfile;</td>
            </tr>
            <tr>
              <td>describe words;</td>
            </tr>
            <tr>
              <td>Load data inpath "/user/hadoop/hivetest" into table words;</td>
            </tr>
            <tr>
              <td>select * from words limit 10;</td>
            </tr>
            <tr>
              <td>select freq, count(1) as f2 from words group by freq sort by f2 desc limit 10;</td>
            </tr>
          </tbody>
        </table>
      </div>)]
    ],
  }
};

class TaskHelp extends React.Component{

  state = {
    current: 'spark',
  }

  render() {
    const { current } = this.state;
    const job = demoSpecification[current];

    return (<div style={{padding: '30px 50px', backgroundColor: '#fff'}}>
      <fieldset className="ui-fieldset">
        <legend>帮助中心</legend>
        <ul className={Style.tags}>
          <li className={current === 'spark' ? 'active' : ''}>
            <a onClick={()=>this.setState({'current': 'spark'})}>SparkSubmit</a>
          </li>
          <li className={current === 'hadoop' ? 'active' : ''}>
            <a onClick={()=>this.setState({'current': 'hadoop'})}>HadoopJava</a>
          </li>
          <li className={current === 'command' ? 'active' : ''}>
            <a onClick={()=>this.setState({'current': 'command'})}>Command</a>
          </li>
          <li className={current === 'hive' ? 'active' : ''}>
            <a onClick={()=>this.setState({'current': 'hive'})}>HiveJob</a>
          </li>
        </ul>
        <section className={Style.section}>
          <header>
            {job.title}
            {job.demoFile ? (
              <Button className={Style.download} size="small"
                onClick={() => downloadFile(job.demoFile)}
              >示例源码下载</Button>
            ) : null}
            {job.jarFile ? (
              <Button className={Style.download} size="small"
                onClick={() => downloadFile(job.jarFile)}
              >Jar包下载</Button>
            ) : null}
          </header>
          <p>{job.description}</p>
          <figure>
            <figcaption className={Style.caption}>配置内容：</figcaption>
            <img className={Style.noteImg} src={job.diagram} />
          </figure>
          <h3 className={Style.caption}>内容说明：</h3>
          <table className={Style.noteTable}>
            <thead>
              <tr>
                <th style={{width: 145}}>配置项</th>
                <th>说明</th>
              </tr>
            </thead>
            <tbody>
              {job.params.map((p, index) => (
                <tr key={index}>
                  <td>{p[0]}</td>
                  <td>{p[1]}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </fieldset>
    </div>)
  }
}

export default TaskHelp;
