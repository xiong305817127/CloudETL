package com.idatrix.resource.catalog.es.utils;

import com.alibaba.fastjson.JSONObject;
import com.idatrix.resource.catalog.es.bean.EsResultBean;
import com.idatrix.resource.catalog.es.exception.EsSearchException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

/**
 * ES工具类
 *
 * @author wzl
 */
public class ElasticsearchUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchUtil.class);

    private static final String COMMA = ",";
    private static final String EQUAL = "=";

    @Autowired
    private Client transportClient;

    private static Client client;

    @PostConstruct
    public void init() {
        client = this.transportClient;
    }

    private ElasticsearchUtil() {
    }

    public static ElasticsearchUtil of() {
        return new ElasticsearchUtil();
    }

    /**
     * 创建索引
     */
    public static boolean createIndex(String index) {
        if (!isIndexExist(index)) {
            LOGGER.info("Index is not exits!");
        }
        CreateIndexResponse indexResponse =
                client.admin().indices().prepareCreate(index).execute().actionGet();

        return indexResponse.isAcknowledged();
    }

    /**
     * 删除索引
     */
    public static boolean deleteIndex(String index) {
        if (!isIndexExist(index)) {
            LOGGER.info("Index is not exits!");
        }
        DeleteIndexResponse dResponse =
                client.admin().indices().prepareDelete(index).execute().actionGet();
        if (dResponse.isAcknowledged()) {
            LOGGER.info("delete index " + index + "  successfully!");
        } else {
            LOGGER.info("Fail to delete index " + index);
        }
        return dResponse.isAcknowledged();
    }

    /**
     * 判断索引是否存在
     */
    public static boolean isIndexExist(String index) {
        IndicesExistsResponse inExistsResponse =
                client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet();
        if (inExistsResponse.isExists()) {
            LOGGER.info("Index [" + index + "] is exist!");
        } else {
            LOGGER.info("Index [" + index + "] is not exist!");
        }
        return inExistsResponse.isExists();
    }

    /**
     * 索引文档,指定ID
     */
    public static String indexDoc(JSONObject doc, String index, String type, String id) {
        return client.prepareIndex(index, type, id).setSource(doc).get().getId();
    }

    /**
     * 索引文档,不指定ID
     *
     * @param index 索引，类似数据库
     * @param type 类型，类似表
     */
    public static String indexDoc(JSONObject doc, String index, String type) {
        return client.prepareIndex(index, type).setSource(doc).get().getId();
    }

    /**
     * 删除文档
     */
    public static String deleteDoc(String index, String type, String id) {
        return client.prepareDelete(index, type, id).execute().actionGet().getId();
    }

    /**
     * 更新文档
     *
     * @param doc 要更新的文档
     */
    public static void update(JSONObject doc, String index, String type, String id) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(index).type(type).id(id).doc(doc);
        client.update(updateRequest);
    }

    /**
     * 获取文档
     *
     * @param fields 需要返回的字段，逗号分隔（默认为全部字段）
     */
    public static Map<String, Object> get(String index, String type, String id, String fields) {
        GetRequestBuilder getRequestBuilder = client.prepareGet(index, type, id);
        if (StringUtils.isNotEmpty(fields)) {
            getRequestBuilder.setFetchSource(fields.split(COMMA), null);
        }
        GetResponse getResponse = getRequestBuilder.execute().actionGet();
        return getResponse.getSource();
    }

    /**
     * 使用分词查询
     */
    public static EsResultBean search(
            String index,
            String type,
            boolean matchPhrase,
            String highlightField,
            String matchStr) {
        return search(index, type, null, null, matchPhrase, highlightField, matchStr);
    }

    /**
     * 使用分词查询
     *
     * @param index 索引名称
     * @param type 类型名称,可传入多个type逗号分隔
     * @param fields 需要显示的字段，逗号分隔（默认为全部字段）
     * @param sortField 排序字段
     * @param matchPhrase true 使用，短语精准匹配
     * @param highlightField 高亮字段
     * @param matchStr 过滤条件（xxx=111,aaa=222）
     */
    public static EsResultBean search(
            String index,
            String type,
            String fields,
            String sortField,
            boolean matchPhrase,
            String highlightField,
            String matchStr) {

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
        if (StringUtils.isNotEmpty(type)) {
            searchRequestBuilder.setTypes(type.split(COMMA));
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 搜索的的字段
        if (StringUtils.isNotEmpty(matchStr)) {
            for (String s : matchStr.split(COMMA)) {
                String[] ss = s.split(EQUAL);
                if (ss.length > 1) {
                    if (matchPhrase == Boolean.TRUE) {
                        boolQuery.must(QueryBuilders
                                .matchPhraseQuery(s.split(EQUAL)[0], s.split(EQUAL)[1]));
                    } else {
                        boolQuery.must(QueryBuilders
                                .matchQuery(s.split(EQUAL)[0], s.split(EQUAL)[1]));
                    }
                }
            }
        }

        // 高亮
        if (StringUtils.isNotEmpty(highlightField)) {
            searchRequestBuilder.addHighlightedField(highlightField);
        }

        searchRequestBuilder.setQuery(boolQuery);

        if (StringUtils.isNotEmpty(fields)) {
            searchRequestBuilder.setFetchSource(fields.split(COMMA), null);
        }
        searchRequestBuilder.setFetchSource(true);

        if (StringUtils.isNotEmpty(sortField)) {
            searchRequestBuilder.addSort(sortField, SortOrder.DESC);
        }

        LOGGER.info("\n{}", searchRequestBuilder);

        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        if (searchResponse.status().getStatus() != HttpServletResponse.SC_OK) {
            throw new EsSearchException("查询无结果");
        }
        return handleSearchResult(searchResponse.getHits(), highlightField);
    }

    /**
     * 处理查询结果
     */
    private static EsResultBean handleSearchResult(SearchHits searchHits, String highlightField) {
        EsResultBean result = new EsResultBean();
        result.setTotal(searchHits.getTotalHits());
        result.setMaxScore(searchHits.getMaxScore());

        List<EsResultBean.HitsBean> hits = new ArrayList<>();
        for (SearchHit searchHit : searchHits.getHits()) {
            EsResultBean.HitsBean hitsBean = new EsResultBean.HitsBean();
            // 复制index type id
            BeanUtils.copyProperties(searchHit, hitsBean);
            hitsBean.setScore(searchHit.getScore());
            if (CollectionUtils.isEmpty(searchHit.getHighlightFields())) {
                continue;
            }
            hitsBean.setHighlight(
                    handleHighlight(searchHit.getHighlightFields().get(highlightField)));
            hits.add(hitsBean);
        }
        result.setHits(hits);
        return result;
    }

    // 处理高亮
    private static EsResultBean.HitsBean.HighlightBean handleHighlight(
            HighlightField highlightField) {
        EsResultBean.HitsBean.HighlightBean highlight = new EsResultBean.HitsBean.HighlightBean();
        List<String> content = new ArrayList<>();
        Text[] texts = highlightField.getFragments();
        if (texts == null || texts.length == 0) {
            return null;
        }
        for (Text text : texts) {
            content.add(text.string());
        }
        highlight.setContent(content);
        return highlight;
    }
}
