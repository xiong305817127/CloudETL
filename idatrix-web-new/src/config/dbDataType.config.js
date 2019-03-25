/**
 * 数据库数据类型配置
 * mysql、hive、hbase
 */
export default {
  mysql:{
    //存在精度的数据类型
    existPrecision:new Map([
      ["DECIMAL",{maxPrecision:30,defaultPrecision:0}],
      ["NUMERIC",{maxPrecision:30,defaultPrecision:0}],
      ["FLOAT",{maxPrecision:30,defaultPrecision:0}],
    //  ["REAL",{maxPrecision:30,defaultPrecision:0}],
      ["DOUBLE",{maxPrecision:30,defaultPrecision:0}],
    ]),
    //存在长度的数据类型
    existLength:new Map([
      ["TINYINT",{maxLength:255,defaultLength:4,require:false}],
      ["SMALLINT",{maxLength:255,defaultLength:6,require:false}],
      ["MEDIUMINT",{maxLength:255,defaultLength:9,require:false}],
      ["INT",{maxLength:255,defaultLength:11,require:false}],
      ["BIGINT",{maxLength:255,defaultLength:20,require:false}],
      ["INTEGER",{maxLength:255,defaultLength:11,require:false}],
      ["DECIMAL",{maxLength:65,defaultLength:10,require:false}],
      ["NUMERIC",{maxLength:65,defaultLength:10,require:false}],
      ["FLOAT",{maxLength:65,defaultLength:0,require:false}],
     // ["REAL",{maxLength:65,defaultLength:0,require:false}],
      ["DOUBLE",{maxLength:65,defaultLength:0,require:false}],
      ["BIT",{maxLength:64,defaultLength:1,require:false}],
      ["CHAR",{maxLength:255,defaultLength:1,require:false}],
      ["VARCHAR",{maxLength:21844,require:true}],
      ["BINARY",{maxLength:255,defaultLength:1,require:false}],
      ["VARBINARY",{maxLength:65532,require:true}],
      ["YEAR",{maxLength:4,defaultLength:4,require:false}],
    ]),
    //其他类型
    otherType:[
      "TINYBLOB","BLOB","MEDIUMBLOB","LONGBLOB",
      "TINYTEXT","TEXT","MEDIUMTEXT","LONGTEXT",
      "DATE","TIME","DATETIME","TIMESTAMP"
    /*  "GEOMETRY","POINT","LINESTRING","POLYGON",
      "MULTIPOINT","MULTILINESTRING","MULTIPOLYGON",
      "GEOMETRYCOLLECTION"*/
    ]
  },
  hive:{
    //存在精度的数据类型
    existPrecision:new Map([
      ["FLOAT",{maxPrecision:30,defaultPrecision:0}],
      ["DOUBLE",{maxPrecision:30,defaultPrecision:0}],
    ]),
    //存在长度的数据类型
    existLength:new Map([
      ["TINYINT",{maxLength:255,defaultLength:4,require:false}],
      ["SMALLINT",{maxLength:255,defaultLength:6,require:false}],
      ["INT",{maxLength:255,defaultLength:11,require:false}],
      ["BIGINT",{maxLength:255,defaultLength:20,require:false}],
      ["FLOAT",{maxLength:65,defaultLength:0,require:false}],
      ["DOUBLE",{maxLength:65,defaultLength:0,require:false}],
      ["BINARY",{maxLength:255,defaultLength:1,require:false}]
    ]),
     //其他类型
    otherType:[
      "BOOLEAN","TIMESTAMP","STRING"
    ]
  },
  hbase:{
    //存在精度的数据类型
    existPrecision:new Map([
      ["DOUBLE",{maxPrecision:30,defaultPrecision:0}],
    ]),
    //存在长度的数据类型
    existLength:new Map([
      ["VARCHAR",{maxLength:21845,require:true}],
      ["INTEGER",{maxLength:255,defaultLength:11,require:false}],
      ["DOUBLE",{maxLength:65,defaultLength:0,require:false}],
    ]),
     //其他类型
    otherType:[
      "DATE"
    ]
  }
};


/*

  // mysql: ['int', 'float', 'double', 'smallint', 'bigint', 'numeric', 'bit', 'real', 'varchar', 'char',
  //   date, 'time', 'year', 'datetime', 'timestamp', 'text', 'longtext', 'blob', 'longblob', 'enum', 'set',
  //   binary, 'point', 'decimal', 'mediumint', 'raw',
  // ],
  

 mysql: ['tinyint', 'smallint', 'numeric', 'decimal', 'real', 'double', 'float', 'bigint', 'integer', 'int', 'mediumint',
    'date', 'datetime', 'timestamp', 'time',
    'char', 'varchar', 'tinyblob', 'blob', 'longblob',
  ],
  hive: ['tinyint', 'smallint', 'int', 'bigint', 'boolean', 'float', 'double', 'string', 'timestamp', 'binary'],
  hbase: ['varchar', 'integer', 'date', 'double'],
  orcle: ['char','long','blob','date','timestamp','clob','varchar','varchar2','number','nblob'],

 */