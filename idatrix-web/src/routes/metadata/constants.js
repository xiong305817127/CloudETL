
//存在精度的数据类型s
const existPrecision = new Map([
	["DECIMAL",{maxPrecision:30,defaultPrecision:0}],
	["NUMERIC",{maxPrecision:30,defaultPrecision:0}],
	["FLOAT",{maxPrecision:30,defaultPrecision:0}],
	["REAL",{maxPrecision:30,defaultPrecision:0}],
	["DOUBLE",{maxPrecision:30,defaultPrecision:0}],
]);
//存在长度的数据类型
const existLength = new Map([
	["TINYINT",{maxLength:255,defaultLength:4,require:false}],
	["SMALLINT",{maxLength:255,defaultLength:6,require:false}],
	["MEDIUMINT",{maxLength:255,defaultLength:9,require:false}],
	["INT",{maxLength:255,defaultLength:11,require:false}],
	["INTTEGER",{maxLength:255,defaultLength:11,require:false}],
	["DECIMAL",{maxLength:65,defaultLength:10,require:false}],
	["NUMERIC",{maxLength:65,defaultLength:10,require:false}],
	["FLOAT",{maxLength:65,defaultLength:0,require:false}],
	["REAL",{maxLength:65,defaultLength:0,require:false}],
	["DOUBLE",{maxLength:65,defaultLength:0,require:false}],
	["BIT",{maxLength:64,defaultLength:1,require:false}],
	["CHAR",{maxLength:255,defaultLength:1,require:false}],
	["VARCHAR",{maxLength:21845,require:true}],
	["BINARY",{maxLength:255,defaultLength:1,require:false}],
	["VARBINARY",{maxLength:65535,require:true}],
	["YEAR",{maxLength:4,defaultLength:4,require:false}],
]);
//其他类型
const otherType = [
	"TINYBLOB","BLOB","MEDIUMBLOB","LONGBLOB",
	"TINYTEXT","TEXT","MEDIUMTEXT","LONGTEXT",
	"DATE","TIME","DATETIME","TIMESTAMP","YEAR",
	"GEOMETRY","POINT","LINESTRING","POLYGON",
	"MULTIPOINT","MULTILINESTRING","MULTIPOLYGON",
	"GEOMETRYCOLLECTION"
];