/**
 * Created by Administrator on 2018/4/20.
 */

export default {
    VERSION: "Saiku-3.15",
    LICENSE: {},
    BIPLUGIN: false,
    BIPLUGIN5: false,
    BASE_URL: window.location.origin,
    TOMCAT_WEBAPP: "/saiku",
    REST_MOUNT_POINT: "/rest/saiku/",
    DIMENSION_PREFETCH: true,
    DIMENSION_SHOW_ALL: true,
    /*
     * Valid values for DIMENSION_HIDE_HIERARCHY:
     * 1) NONE
     * 2) SINGLE_LEVEL
     * 3) ALL
     */
    DIMENSION_HIDE_HIERARCHY: 'SINGLE_LEVEL',
    ERROR_LOGGING: false,
    I18N_LOCALE: "en",
    // number of erroneous ajax calls in a row before UI cant recover
    ERROR_TOLERANCE: 3,
    QUERY_PROPERTIES: {
        'saiku.olap.query.automatic_execution': true,
        'saiku.olap.query.nonempty': true,
        'saiku.olap.query.nonempty.rows': true,
        'saiku.olap.query.nonempty.columns': true,
        'saiku.ui.render.mode' : 'table',
        'saiku.olap.query.filter' : true,
        'saiku.olap.result.formatter' : "flattened"
    },
    REPOSITORY_LAZY: false,
    TABLE_LAZY: true,          // Turn lazy loading off / on
    TABLE_LAZY_SIZE: 1000,     // Initial number of items to be rendered
    TABLE_LAZY_LOAD_ITEMS: 20,       // Additional item per scroll
    TABLE_LAZY_LOAD_TIME: 20,  // throttling call of lazy loading items
    /* Valid values for CELLSET_FORMATTER:
     * 1) flattened
     * 2) flat
     */
    CELLSET_FORMATTER: "flattened",
    // limits the number of rows in the result
    // 0 - no limit
    RESULT_LIMIT: 0,
    MEMBERS_FROM_RESULT: true,
    MEMBERS_LIMIT: 3000,
    MEMBERS_SEARCH_LIMIT: 75,
    ALLOW_IMPORT_EXPORT: false,
    ALLOW_PARAMETERS: true,
    PLUGINS: [
        "Chart"
    ],
    DEFAULT_VIEW_STATE: 'view', // could be 'edit' as well
    DEMO: false,
    TELEMETRY_SERVER: 'http://telemetry.analytical-labs.com:7000',
    LOCALSTORAGE_EXPIRATION: 10 * 60 * 60 * 1000 /* 10 hours, in ms */,
    UPGRADE: true,
    EVALUATION_PANEL_LOGIN: true,
    QUERY_OVERWRITE_WARNING: true,
    MAPS: true,
    MAPS_TYPE: 'OSM', // OSM || GMAPS
    MAPS_TILE_LAYER: {
        OSM: {
            'map_marker': 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
            'map_heat': 'https://otile{s}-s.mqcdn.com/tiles/1.0.0/sat/{z}/{x}/{y}.png'
        },
        GMAPS: {
        }
    },
    MAPS_OPTIONS: {
        OSM: {
            maxZoom: 18,
            attribution: '© <a href="http://osm.org/copyright" target="_blank">OpenStreetMap</a>'
        },
        GMAPS: {
        }
    },
    MAPS_OSM_NOMINATIM: 'https://nominatim.openstreetmap.org/', // http://wiki.openstreetmap.org/wiki/Nominatim
    DATA_SOURCES_LOOKUP: false,
    DEFAULT_REPORT_SHOW: false, // true/false
    DEFAULT_REPORTS: {
        'admin': [
            {
                path: 'ADD_PATH1', // example: /homes/home:admin/chart.saiku
                visible: false    // true/false
            }
        ],
        '_': [
            {
                path: 'ADD_PATH2',
                visible: false
            }
        ],
        'ROLE_ADMIN': [
            {
                path: 'ADD_PATH3',
                visible: false
            }
        ]
    },
    PARENT_MEMBER_DIMENSION: false,
    EXT_DATASOURCE_PROPERTIES: false,
    SHOW_USER_MANAGEMENT: true,
    SHOW_REFRESH_NONADMIN: false,
    EMPTY_VALUE_CHARACTER: '-',
    HIDE_EMPTY_ROWS: true,
    MEASURE_GROUPS_COLLAPSED: false,
    ORBIS_AUTH: {
        enabled: false,
        cookieName: 'SAIKU_AUTH_PRINCIPAL'
    },
    SCHEMA_EDITOR: {
        // The `^` matches beginning of input.
        STAR_SCHEMA_FACT_TABLE: /^fact_|^f_/i,
        STAR_SCHEMA_DIMENSION_TABLE: /^dimension_|^dim_|^d_/i,
        STAR_SCHEMA_MEASURE_COLUMN: /^measure_|^m_/i,
        // The `$` matches end of input.
        STAR_SCHEMA_MEASURE_AGGREGATION_COLUMN: /_sum$|_avg$|_count$|_min$|_max$/i
    },
    ALLOW_TABLE_DATA_COLLAPSE: false,
    ALLOW_AXIS_COLUMN_TITLE_TABLE: true,
    COLUMN_TITLE_TABLE_USE_LEVEL_CAPTION_NAME: true,
    INTRO_FILE_NAME: 'Workspace',
    // For more options, see: http://introjs.com/docs/intro/options/
    INTRO_DEFAULT_OPTIONS: {
        showStepNumbers: true,
        showBullets: false,
        showProgress: true
    },
    OZP_IWC_ENABLE: false,
    OZP_IWC_CLIENT_URI: 'http://aml-development.github.io/ozp-iwc',
    // /{minor}/{major}/{action} ("/application/json/view")
    // or
    // /{minor}/{major}/{action}/{handlerId} ("/application/json/view/123")
    OZP_IWC_REFERENCE_PATH: '/application/display/help',
    OZP_IWC_CONFIG: {
        label: 'Saiku Analytics',
        icon: 'https://avatars0.githubusercontent.com/u/1043666?v=3&s=32'
    }
};

