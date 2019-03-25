/**
 * Created by Administrator on 2018/4/19.
 */
import React from 'react';
import { Table } from 'antd';
import SaikuTableRenderer from './table';
import Settings from './setting';
import './index.less'

const data = [
    [
        {
            "value": "Store Country",
            "type": "ROW_HEADER_HEADER",
            "properties": {
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store Country]"
            }
        },
        {
            "value": "Store State",
            "type": "ROW_HEADER_HEADER",
            "properties": {
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store State]"
            }
        },
        {
            "value": "Grocery Sqft",
            "type": "COLUMN_HEADER",
            "properties": {
                "uniquename": "[Measures].[Grocery Sqft]",
                "hierarchy": "[Measures]",
                "dimension": "Measures",
                "level": "[Measures].[MeasuresLevel]"
            }
        }
    ],
    [
        {
            "value": "Canada",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[Canada]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store Country]"
            }
        },
        {
            "value": "BC",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[Canada].[BC]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store State]"
            }
        },
        {
            "value": "43,881",
            "type": "DATA_CELL",
            "properties": {
                "position": "0:1",
                "raw": "43881.0"
            }
        }
    ],
    [
        {
            "value": "Mexico",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[Mexico]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store Country]"
            }
        },
        {
            "value": "DF",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[Mexico].[DF]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store State]"
            }
        },
        {
            "value": "22,450",
            "type": "DATA_CELL",
            "properties": {
                "position": "0:3",
                "raw": "22450.0"
            }
        }
    ],
    [
        {
            "value": "Mexico",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[Mexico]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store Country]"
            }
        },
        {
            "value": "Guerrero",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[Mexico].[Guerrero]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store State]"
            }
        },
        {
            "value": "17,475",
            "type": "DATA_CELL",
            "properties": {
                "position": "0:4",
                "raw": "17475.0"
            }
        }
    ],
    [
        {
            "value": "Mexico",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[Mexico]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store Country]"
            }
        },
        {
            "value": "Jalisco",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[Mexico].[Jalisco]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store State]"
            }
        },
        {
            "value": "15,012",
            "type": "DATA_CELL",
            "properties": {
                "position": "0:5",
                "raw": "15012.0"
            }
        }
    ],
    [
        {
            "value": "Mexico",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[Mexico]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store Country]"
            }
        },
        {
            "value": "Veracruz",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[Mexico].[Veracruz]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store State]"
            }
        },
        {
            "value": "26,354",
            "type": "DATA_CELL",
            "properties": {
                "position": "0:6",
                "raw": "26354.0"
            }
        }
    ],
    [
        {
            "value": "Mexico",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[Mexico]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store Country]"
            }
        },
        {
            "value": "Yucatan",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[Mexico].[Yucatan]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store State]"
            }
        },
        {
            "value": "20,141",
            "type": "DATA_CELL",
            "properties": {
                "position": "0:7",
                "raw": "20141.0"
            }
        }
    ],
    [
        {
            "value": "Mexico",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[Mexico]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store Country]"
            }
        },
        {
            "value": "Zacatecas",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[Mexico].[Zacatecas]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store State]"
            }
        },
        {
            "value": "69,133",
            "type": "DATA_CELL",
            "properties": {
                "position": "0:8",
                "raw": "69133.0"
            }
        }
    ],
    [
        {
            "value": "USA",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[USA]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store Country]"
            }
        },
        {
            "value": "CA",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[USA].[CA]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store State]"
            }
        },
        {
            "value": "44,868",
            "type": "DATA_CELL",
            "properties": {
                "position": "0:10",
                "raw": "44868.0"
            }
        }
    ],
    [
        {
            "value": "USA",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[USA]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store Country]"
            }
        },
        {
            "value": "OR",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[USA].[OR]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store State]"
            }
        },
        {
            "value": "34,902",
            "type": "DATA_CELL",
            "properties": {
                "position": "0:11",
                "raw": "34902.0"
            }
        }
    ],
    [
        {
            "value": "USA",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[USA]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store Country]"
            }
        },
        {
            "value": "WA",
            "type": "ROW_HEADER",
            "properties": {
                "uniquename": "[Store].[Stores].[USA].[WA]",
                "hierarchy": "[Store].[Stores]",
                "dimension": "Store",
                "level": "[Store].[Stores].[Store State]"
            }
        },
        {
            "value": "104,152",
            "type": "DATA_CELL",
            "properties": {
                "position": "0:12",
                "raw": "104152.0"
            }
        }
    ]
];

const data1 =  {"cellset":[[{"value":"null","type":"COLUMN_HEADER","properties":{}},{"value":"null","type":"COLUMN_HEADER","properties":{}},{"value":"20319","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[20319]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"21215","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[21215]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"22478","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[22478]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"23112","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[23112]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"23593","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[23593]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"23598","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[23598]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"23688","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[23688]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"23759","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[23759]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"24597","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[24597]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"27694","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[27694]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"28206","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[28206]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"30268","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[30268]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"30584","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[30584]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"30797","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[30797]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"33858","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[33858]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"34452","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[34452]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"34791","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[34791]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"36509","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[36509]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"38382","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[38382]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}},{"value":"39696","type":"COLUMN_HEADER","properties":{"uniquename":"[Store].[Store Size in SQFT].[39696]","hierarchy":"[Store].[Store Size in SQFT]","dimension":"Store","level":"[Store].[Store Size in SQFT].[Store Sqft]"}}],[{"value":"Store Country","type":"ROW_HEADER_HEADER","properties":{"hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Store City","type":"ROW_HEADER_HEADER","properties":{"hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}}],[{"value":"Canada","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Canada]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Vancouver","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Canada].[BC].[Vancouver]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:1"}},{"value":"23,112","type":"DATA_CELL","properties":{"position":"3:1","raw":"23112.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:1"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:1"}}],[{"value":"Canada","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Canada]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Victoria","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Canada].[BC].[Victoria]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:2"}},{"value":"34,452","type":"DATA_CELL","properties":{"position":"15:2","raw":"34452.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:2"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:2"}}],[{"value":"Mexico","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Mexico City","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico].[DF].[Mexico City]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:4"}},{"value":"36,509","type":"DATA_CELL","properties":{"position":"17:4","raw":"36509.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:4"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:4"}}],[{"value":"Mexico","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Acapulco","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico].[Guerrero].[Acapulco]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:5"}},{"value":"23,593","type":"DATA_CELL","properties":{"position":"4:5","raw":"23593.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:5"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:5"}}],[{"value":"Mexico","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Guadalajara","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico].[Jalisco].[Guadalajara]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:6"}},{"value":"24,597","type":"DATA_CELL","properties":{"position":"8:6","raw":"24597.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:6"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:6"}}],[{"value":"Mexico","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Orizaba","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico].[Veracruz].[Orizaba]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:7"}},{"value":"34,791","type":"DATA_CELL","properties":{"position":"16:7","raw":"34791.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:7"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:7"}}],[{"value":"Mexico","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Merida","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico].[Yucatan].[Merida]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:8"}},{"value":"30,797","type":"DATA_CELL","properties":{"position":"13:8","raw":"30797.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:8"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:8"}}],[{"value":"Mexico","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Camacho","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico].[Zacatecas].[Camacho]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:9"}},{"value":"23,759","type":"DATA_CELL","properties":{"position":"7:9","raw":"23759.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:9"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:9"}}],[{"value":"Mexico","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Hidalgo","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico].[Zacatecas].[Hidalgo]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:10"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:10"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:10"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:10"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:10"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:10"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:10"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:10"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:10"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:10"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:10"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:10"}},{"value":"30,584","type":"DATA_CELL","properties":{"position":"12:10","raw":"30584.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:10"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:10"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:10"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:10"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:10"}},{"value":"38,382","type":"DATA_CELL","properties":{"position":"18:10","raw":"38382.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:10"}}],[{"value":"USA","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Beverly Hills","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA].[CA].[Beverly Hills]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:12"}},{"value":"23,688","type":"DATA_CELL","properties":{"position":"6:12","raw":"23688.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:12"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:12"}}],[{"value":"USA","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Los Angeles","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA].[CA].[Los Angeles]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:13"}},{"value":"23,598","type":"DATA_CELL","properties":{"position":"5:13","raw":"23598.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:13"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:13"}}],[{"value":"USA","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"San Francisco","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA].[CA].[San Francisco]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:14"}},{"value":"22,478","type":"DATA_CELL","properties":{"position":"2:14","raw":"22478.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:14"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:14"}}],[{"value":"USA","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Portland","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA].[OR].[Portland]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"20,319","type":"DATA_CELL","properties":{"position":"0:15","raw":"20319.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:15"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:15"}}],[{"value":"USA","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Salem","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA].[OR].[Salem]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:16"}},{"value":"27,694","type":"DATA_CELL","properties":{"position":"9:16","raw":"27694.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:16"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:16"}}],[{"value":"USA","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Bellingham","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA].[WA].[Bellingham]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:17"}},{"value":"28,206","type":"DATA_CELL","properties":{"position":"10:17","raw":"28206.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:17"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:17"}}],[{"value":"USA","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Bremerton","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA].[WA].[Bremerton]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:18"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:18"}},{"value":"39,696","type":"DATA_CELL","properties":{"position":"19:18","raw":"39696.0"}}],[{"value":"USA","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Seattle","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA].[WA].[Seattle]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:19"}},{"value":"21,215","type":"DATA_CELL","properties":{"position":"1:19","raw":"21215.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:19"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:19"}}],[{"value":"USA","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Spokane","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA].[WA].[Spokane]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:20"}},{"value":"30,268","type":"DATA_CELL","properties":{"position":"11:20","raw":"30268.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"14:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:20"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:20"}}],[{"value":"USA","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store Country]"}},{"value":"Tacoma","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA].[WA].[Tacoma]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store City]"}},{"value":"","type":"DATA_CELL","properties":{"position":"0:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"1:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"2:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"3:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"4:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"5:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"6:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"7:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"8:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"9:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"10:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"11:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"12:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"13:21"}},{"value":"33,858","type":"DATA_CELL","properties":{"position":"14:21","raw":"33858.0"}},{"value":"","type":"DATA_CELL","properties":{"position":"15:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"16:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"17:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"18:21"}},{"value":"","type":"DATA_CELL","properties":{"position":"19:21"}}]],"rowTotalsLists":null,"colTotalsLists":null,"runtime":29,"error":null,"height":21,"width":22,"query":{"queryModel":{"axes":{"FILTER":{"mdx":null,"filters":[],"sortOrder":null,"sortEvaluationLiteral":null,"hierarchizeMode":null,"location":"FILTER","hierarchies":[],"nonEmpty":false,"aggregators":[]},"COLUMNS":{"mdx":null,"filters":[],"sortOrder":null,"sortEvaluationLiteral":null,"hierarchizeMode":null,"location":"COLUMNS","hierarchies":[{"mdx":null,"filters":[],"sortOrder":null,"sortEvaluationLiteral":null,"hierarchizeMode":null,"name":"[Store].[Store Size in SQFT]","caption":"Store Size in SQFT","dimension":"Store","levels":{"Store Sqft":{"mdx":null,"filters":[],"name":"Store Sqft","caption":"Store Sqft","selection":{"type":"INCLUSION","members":[],"parameterName":null},"aggregators":[],"measureAggregators":[]}},"cmembers":{}}],"nonEmpty":true,"aggregators":[]},"ROWS":{"mdx":null,"filters":[],"sortOrder":null,"sortEvaluationLiteral":null,"hierarchizeMode":null,"location":"ROWS","hierarchies":[{"mdx":null,"filters":[],"sortOrder":null,"sortEvaluationLiteral":null,"hierarchizeMode":null,"name":"[Store].[Stores]","caption":"Stores","dimension":"Store","levels":{"Store Country":{"mdx":null,"filters":[],"name":"Store Country","caption":"Store Country","selection":{"type":"INCLUSION","members":[],"parameterName":null},"aggregators":[],"measureAggregators":[]},"Store City":{"mdx":null,"filters":[],"name":"Store City","caption":"Store City","selection":{"type":"INCLUSION","members":[],"parameterName":null},"aggregators":[],"measureAggregators":[]}},"cmembers":{}}],"nonEmpty":true,"aggregators":[]}},"visualTotals":false,"visualTotalsPattern":null,"lowestLevelsOnly":false,"details":{"axis":"COLUMNS","location":"BOTTOM","measures":[{"name":"Store Sqft","uniqueName":"[Measures].[Store Sqft]","caption":"Store Sqft","type":"EXACT","aggregators":[]}]},"calculatedMeasures":[],"calculatedMembers":[]},"cube":{"uniqueName":"[foodmart].[FoodMart].[FoodMart].[Store]","name":"Store","connection":"foodmart","catalog":"FoodMart","schema":"FoodMart","caption":null,"visible":false},"mdx":"WITH\r\nSET [~COLUMNS] AS\r\n    {[Store].[Store Size in SQFT].[Store Sqft].Members}\r\nSET [~ROWS] AS\r\n    Hierarchize({{[Store].[Stores].[Store Country].Members}, {[Store].[Stores].[Store City].Members}})\r\nSELECT\r\nNON EMPTY CrossJoin([~COLUMNS], {[Measures].[Store Sqft]}) ON COLUMNS,\r\nNON EMPTY [~ROWS] ON ROWS\r\nFROM [Store]","name":"7320E4E8-4ED4-3F14-81A5-78DECEF12235","parameters":{},"plugins":{},"properties":{"saiku.olap.query.automatic_execution":true,"saiku.olap.query.nonempty":true,"saiku.olap.query.nonempty.rows":true,"saiku.olap.query.nonempty.columns":true,"saiku.ui.render.mode":"table","saiku.olap.query.filter":true,"saiku.olap.result.formatter":"flattened","org.saiku.query.explain":true,"saiku.olap.query.drillthrough":true,"org.saiku.connection.scenario":false},"metadata":{},"queryType":"OLAP","type":"QUERYMODEL"},"topOffset":2,"leftOffset":0}

const columns1 = [
    {
    title: 'Name',
    dataIndex: 'name',
    key: 'name',
    width: 100,
    fixed: 'left',
    filters: [{
        text: 'Joe',
        value: 'Joe',
    }, {
        text: 'John',
        value: 'John',
    }],
    onFilter: (value, record) => record.name.indexOf(value) === 0,
}, {
    title: 'Other',
    children: [{
        title: 'Age',
        dataIndex: 'age',
        key: 'age',
        width: 200,
        sorter: (a, b) => a.age - b.age,
    }, {
        title: 'Address',
        children: [{
            title: 'Street',
            dataIndex: 'street',
            key: 'street',
            width: 200,
        }, {
            title: 'Block',
            children: [{
                title: 'Building',
                dataIndex: 'building',
                key: 'building',
                width: 100,
            }, {
                title: 'Door No.',
                dataIndex: 'number',
                key: 'number',
                width: 100,
            }],
        }],
    }],
}, {
    title: 'Company',
    children: [{
        title: 'Company Address',
        dataIndex: 'companyAddress',
        key: 'companyAddress',
    }, {
        title: 'Company Name',
        dataIndex: 'companyName',
        key: 'companyName',
    }],
}, {
    title: 'Gender',
    dataIndex: 'gender',
    key: 'gender',
    width: 60,
    fixed: 'right',
}];


const data3 = {"cellset":[[{"value":"Store State","type":"ROW_HEADER_HEADER","properties":{"hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store State]"}},{"value":"Store Sqft","type":"COLUMN_HEADER","properties":{"uniquename":"[Measures].[Store Sqft]","hierarchy":"[Measures]","dimension":"Measures","level":"[Measures].[MeasuresLevel]"}}],[{"value":"BC","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Canada].[BC]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store State]"}},{"value":"57,564","type":"DATA_CELL","properties":{"position":"0:0","raw":"57564.0"}}],[{"value":"DF","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico].[DF]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store State]"}},{"value":"36,509","type":"DATA_CELL","properties":{"position":"0:1","raw":"36509.0"}}],[{"value":"Guerrero","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico].[Guerrero]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store State]"}},{"value":"23,593","type":"DATA_CELL","properties":{"position":"0:2","raw":"23593.0"}}],[{"value":"Jalisco","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico].[Jalisco]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store State]"}},{"value":"24,597","type":"DATA_CELL","properties":{"position":"0:3","raw":"24597.0"}}],[{"value":"Veracruz","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico].[Veracruz]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store State]"}},{"value":"34,791","type":"DATA_CELL","properties":{"position":"0:4","raw":"34791.0"}}],[{"value":"Yucatan","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico].[Yucatan]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store State]"}},{"value":"30,797","type":"DATA_CELL","properties":{"position":"0:5","raw":"30797.0"}}],[{"value":"Zacatecas","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[Mexico].[Zacatecas]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store State]"}},{"value":"92,725","type":"DATA_CELL","properties":{"position":"0:6","raw":"92725.0"}}],[{"value":"CA","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA].[CA]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store State]"}},{"value":"69,764","type":"DATA_CELL","properties":{"position":"0:7","raw":"69764.0"}}],[{"value":"OR","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA].[OR]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store State]"}},{"value":"48,013","type":"DATA_CELL","properties":{"position":"0:8","raw":"48013.0"}}],[{"value":"WA","type":"ROW_HEADER","properties":{"uniquename":"[Store].[Stores].[USA].[WA]","hierarchy":"[Store].[Stores]","dimension":"Store","level":"[Store].[Stores].[Store State]"}},{"value":"153,243","type":"DATA_CELL","properties":{"position":"0:9","raw":"153243.0"}}]],"rowTotalsLists":null,"colTotalsLists":null,"runtime":20,"error":null,"height":11,"width":2,"query":{"queryModel":{"axes":{"FILTER":{"mdx":null,"filters":[],"sortOrder":null,"sortEvaluationLiteral":null,"hierarchizeMode":null,"location":"FILTER","hierarchies":[],"nonEmpty":false,"aggregators":[]},"COLUMNS":{"mdx":null,"filters":[],"sortOrder":null,"sortEvaluationLiteral":null,"hierarchizeMode":null,"location":"COLUMNS","hierarchies":[],"nonEmpty":true,"aggregators":[]},"ROWS":{"mdx":null,"filters":[],"sortOrder":null,"sortEvaluationLiteral":null,"hierarchizeMode":null,"location":"ROWS","hierarchies":[{"mdx":null,"filters":[],"sortOrder":null,"sortEvaluationLiteral":null,"hierarchizeMode":null,"name":"[Store].[Stores]","caption":"Stores","dimension":"Store","levels":{"Store State":{"mdx":null,"filters":[],"name":"Store State","caption":"Store State","selection":{"type":"INCLUSION","members":[],"parameterName":null},"aggregators":[],"measureAggregators":[]}},"cmembers":{}}],"nonEmpty":true,"aggregators":[]}},"visualTotals":false,"visualTotalsPattern":null,"lowestLevelsOnly":false,"details":{"axis":"COLUMNS","location":"BOTTOM","measures":[{"name":"Store Sqft","uniqueName":"[Measures].[Store Sqft]","caption":"Store Sqft","type":"EXACT","aggregators":[]}]},"calculatedMeasures":[],"calculatedMembers":[]},"cube":{"uniqueName":"[foodmart].[FoodMart].[FoodMart].[Store]","name":"Store","connection":"foodmart","catalog":"FoodMart","schema":"FoodMart","caption":null,"visible":false},"mdx":"WITH\r\nSET [~ROWS] AS\r\n    {[Store].[Stores].[Store State].Members}\r\nSELECT\r\nNON EMPTY {[Measures].[Store Sqft]} ON COLUMNS,\r\nNON EMPTY [~ROWS] ON ROWS\r\nFROM [Store]","name":"5A7E8A0F-16E4-2078-938E-A781332644C4","parameters":{},"plugins":{},"properties":{"saiku.olap.query.automatic_execution":true,"saiku.olap.query.nonempty":true,"saiku.olap.query.nonempty.rows":true,"saiku.olap.query.nonempty.columns":true,"saiku.ui.render.mode":"table","saiku.olap.query.filter":true,"saiku.olap.result.formatter":"flattened","org.saiku.query.explain":true,"saiku.olap.query.drillthrough":true,"org.saiku.connection.scenario":false},"metadata":{},"queryType":"OLAP","type":"QUERYMODEL"},"topOffset":1,"leftOffset":0}
/**
 * 把数组转换成表格的列
 * @param args
 */
const getColumns = (args)=>{

    let columns = [];   //第一层级分类
    let i = 0;
    let unique = [];

    for(let index of args){
        if(index.length && index[index.length-1].type !== "DATA_CELL"){
            columns.push(index);
        }else{
            break;
        }
    }
    return columns;
};

/**
 * 通过递归变成对象数组
 */
const getTitleArgs = (columns)=>{

    let unique = [];
    let length = columns.length;





    if(columns.length>1){
        let newArgs = new Set();
        let newIndex = {};
        let m = 0;
        for(let index of columns[i]){
            if(!newArgs.has(index.value)){
                newArgs.add(index.value);
                newIndex = index;
                newIndex.childrens = [];
                unique.push(newIndex);
            }
            newIndex.childrens.push(columns[i+1][m]);
            m++;
        }
    }


};


/**
 * 把数组变为表数据
 * @param args
 */
const getDataSource = (args)=>{
    let m = 0,n,data=[];

    let colums = []; //用来保存列标题
/*    for(let index of args){
        let obj = {};
        n = 0;
        for(let value of index){
            obj[n] = value.value;
            n++;
        }
        data.push({
           key:m,
            ...obj
        });
        m++;
    }*/

    return data;
};

function parseDom(arg) {

    var objE = document.createElement("div");

    objE.innerHTML = arg;

    return objE.childNodes;

};

class Index extends React.Component{

    constructor(props){
        super(props);
        let content = new SaikuTableRenderer();
        this.state= {
            content:content,
            html:null
        };
    }

    componentDidMount(){

        const { content } = this.state;
        let tableDom = this.refs.table;
        
       let html =   content.render(data1,{
            hideEmpty:          Settings.HIDE_EMPTY_ROWS,
            htmlObject:         tableDom,
            batch:              Settings.TABLE_LAZY,
            batchSize:          Settings.TABLE_LAZY_SIZE,
            batchIntervalSize:  Settings.TABLE_LAZY_LOAD_ITEMS,
            batchIntervalTime:  Settings.TABLE_LAZY_LOAD_TIME
        });

        this.setState({
            html:html
        })
    }

    componentDidUpdate() {
        document.getElementsByClassName("table_wrapper")[0].innerHTML = this.state.html;
    }

    render(){

        return(
            <div id="workspace_results">
                <div className="table_wrapper" ref="table"></div>
            </div>
        )
    }
}

export default Index;