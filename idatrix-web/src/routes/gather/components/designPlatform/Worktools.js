import React from 'react';
import { Menu, Icon,Row,Col } from 'antd';
import { connect } from 'dva';
import Style from './Workview.css';
import ToolsItem from '../common/ToolsItem';
const SubMenu = Menu.SubMenu;
import ToolsTitle from '../common/ToolsTitle'


const Worktools = ({ })=>{


  return (
    <div  onDrop={e=>{ e.preventDefault()}}  onDragOver={e=>{ e.preventDefault()}} id="worktools">

      <Menu
            className={Style.menuStyle}
            defaultOpenKeys={['sub13']}
            mode="inline"
            width={215}
            inlineIndent = "0"
           >
          <ToolsTitle title="控件"  type="control"/>
          <SubMenu key="sub13" title={<span style={{paddingLeft:"24px"}}>输入</span>}  >
            <Menu.Item key="A01">
              <ToolsItem type="TableInput" />
            </Menu.Item>
            <Menu.Item key="A02">
              <ToolsItem  type="AccessInput"/>
            </Menu.Item>
            <Menu.Item key="A03">
              <ToolsItem type="CsvInput" />
            </Menu.Item>
            <Menu.Item key="A04">
              <ToolsItem type="TextFileInput" />
            </Menu.Item>
            <Menu.Item key="A05">
              <ToolsItem type="GetFileNames" />
            </Menu.Item>
            <Menu.Item key="A06">
              <ToolsItem type="ExcelInput" />
            </Menu.Item>
          </SubMenu>
          <SubMenu key="sub4"  title={<span style={{paddingLeft:"24px"}}>输出</span>}>
            <Menu.Item key="B01">
              <ToolsItem type="TableOutput"  />
            </Menu.Item>
            <Menu.Item key="B02">
              <ToolsItem type="TextFileOutput"  />
            </Menu.Item>
            <Menu.Item key="B03">
              <ToolsItem type="InsertUpdate"  />
            </Menu.Item>
          </SubMenu>
          {/*<SubMenu key="sub15"  title={<span style={{paddingLeft:"24px"}}>查询</span>}>*/}
            {/*<Menu.Item key="B01">*/}
              {/*<ToolsItem type="TableOutput"  />*/}
            {/*</Menu.Item>*/}
            {/*<Menu.Item key="B02">*/}
              {/*<ToolsItem type="TextFileOutput"  />*/}
            {/*</Menu.Item>*/}
            {/*<Menu.Item key="B03">*/}
              {/*<ToolsItem type="InsertUpdate"  />*/}
            {/*</Menu.Item>*/}
          {/*</SubMenu>*/}
          <SubMenu key="sub12"  title={<span style={{paddingLeft:"24px"}}>大数据</span>}>
            <Menu.Item key="D01">
              <ToolsItem type="HadoopFileInputPlugin"  />
            </Menu.Item>
            <Menu.Item key="D02">
              <ToolsItem type="HadoopFileOutputPlugin"  />
            </Menu.Item>
             <Menu.Item key="D03">
               <ToolsItem type="HBaseOutput"  />
            </Menu.Item>
          </SubMenu>
          <SubMenu key="sub5"  title={<span style={{paddingLeft:"24px"}}>脚本</span>}>
              <Menu.Item key="C01">
                <ToolsItem type="ExecSQL"  />
              </Menu.Item>
              <Menu.Item key="C02">
                <ToolsItem type="ScriptValueMod"  />
              </Menu.Item>
          </SubMenu>
          <SubMenu key="sub7"  title={<span style={{paddingLeft:"24px"}}>转换</span>}>
            <Menu.Item key="D01">
              <ToolsItem type="ValueMapper"  />
            </Menu.Item>
            <Menu.Item key="D02"  >
              <ToolsItem type="SplitFieldToRows3" />
            </Menu.Item>
            <Menu.Item key="D03"  >
              <ToolsItem type="Denormaliser" />
            </Menu.Item>
            <Menu.Item key="D04"  >
              <ToolsItem type="StringCut" />
            </Menu.Item>
            <Menu.Item key="D05"  >
              <ToolsItem  type="Unique"/>
            </Menu.Item>
            <Menu.Item key="D06"  >
              <ToolsItem type="UniqueRowsByHashSet" />
            </Menu.Item>
            <Menu.Item key="D07">
              <ToolsItem type="Constant"  />
            </Menu.Item>
            <Menu.Item key="D08"  >
              <ToolsItem type="GetSlaveSequence" />
            </Menu.Item>
            <Menu.Item key="D09"  >
              <ToolsItem type="Sequence" />
            </Menu.Item>
            <Menu.Item key="D10"  >
              <ToolsItem type="CheckSum" />
            </Menu.Item>
            <Menu.Item key="D11"  >
              <ToolsItem  type="SelectValues"/>
            </Menu.Item>
            <Menu.Item key="D12"  >
              <ToolsItem type="StringOperations" />
            </Menu.Item>
            <Menu.Item key="D13">
              <ToolsItem type="ReplaceString"  />
            </Menu.Item>
            <Menu.Item key="D14"  >
              <ToolsItem type="ClosureGenerator" />
            </Menu.Item>
            <Menu.Item key="D15"  >
              <ToolsItem type="ConcatFields" />
            </Menu.Item>
            <Menu.Item key="D16"  >
              <ToolsItem type="SetValueConstant" />
            </Menu.Item>
            <Menu.Item key="D17"  >
              <ToolsItem  type="FieldSplitter"/>
            </Menu.Item>
            <Menu.Item key="D18"  >
              <ToolsItem type="SortRows" />
            </Menu.Item>
            <Menu.Item key="D19">
              <ToolsItem type="NumberRange"  />
            </Menu.Item>
            <Menu.Item key="D20"  >
              <ToolsItem type="FieldsChangeSequence" />
            </Menu.Item>
            <Menu.Item key="D21"  >
              <ToolsItem type="Flattener" />
            </Menu.Item>
            <Menu.Item key="D22"  >
              <ToolsItem type="Normaliser" />
            </Menu.Item>
            <Menu.Item key="D23"  >
              <ToolsItem  type="Calculator"/>
            </Menu.Item>
            <Menu.Item key="D24"  >
              <ToolsItem type="SetValueField" />
            </Menu.Item>
            <Menu.Item key="D25"  >
              <ToolsItem type="AddXML" />
            </Menu.Item>
          </SubMenu>
          <SubMenu key="sub25"  title={<span style={{paddingLeft:"24px"}}>批量加载</span>}>
              <Menu.Item key="P01"  >
              <ToolsItem type="ElasticSearchBulk" />
            </Menu.Item>
          </SubMenu>
        <ToolsTitle title="开发中"  type="developing"/>
        <SubMenu key="sub100"  title={<span style={{paddingLeft:"24px"}}>开发中</span>}>
          <Menu.Item key="A04"  >
            <ToolsItem type="Update" />
          </Menu.Item>
          <Menu.Item key="A05"  >
            <ToolsItem type="SPECIAL" />
          </Menu.Item>
          <Menu.Item key="A06"  >
            <ToolsItem type="HadoopCopyFilesPlugin" />
          </Menu.Item>
          <Menu.Item key="A07"  >
            <ToolsItem type="SET_VARIABLES" />
          </Menu.Item>
           <Menu.Item key="A08"  >
            <ToolsItem type="DELAY" />
          </Menu.Item>
          <Menu.Item key="A09"  >
            <ToolsItem type="SIMPLE_EVAL" />
          </Menu.Item>
        </SubMenu>
      </Menu>
    </div>
  )
}

export default connect()(Worktools)


