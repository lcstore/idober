package com.lezo.idober.solr.pojo;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import lombok.Data;

import org.apache.solr.client.solrj.beans.Field;

@Data
public class UnifyMovieSolr {
    @Field
    private String id;
    @Field
    private String name;
    @Field
    private String enname;
    @Field
    private String cover;
    @Field
    private String image;
    @Field
    private String lang;
    @Field
    private String release;
    @Field
    private String year;
    @Field
    private Float rate;
    @Field
    private Integer star;
    @Field
    private Integer cost;
    @Field
    private Integer comment;
    @Field
    private List<String> regions;
    @Field
    private List<String> names;
    @Field
    private List<String> directors;
    @Field
    private List<String> actors;
    @Field
    private List<String> genres;
    @Field
    private String editor;
    @Field
    private Integer tcount;
    @Field
    private List<String> torrents;
    @Field
    private Integer scount;
    @Field
    private List<String> subtitles;
    @Field
    private List<String> tags;
    @Field
    private String content;
    @Field
    private Date timestamp;
    @Field
    private List<Date> creation;

    public List<String> getRegions() {
        return doReturn(regions);
    }

    public List<String> getNames() {
        return doReturn(names);
    }

    public List<String> getDirectors() {
        return doReturn(directors);
    }

    public List<String> getActors() {
        return doReturn(actors);
    }

    public List<String> getGenres() {
        return doReturn(genres);
    }

    public List<String> getTorrents() {
        return doReturn(torrents);
    }

    public List<String> getSubtitles() {
        return doReturn(subtitles);
    }

    public List<String> getTags() {
        return doReturn(tags);
    }

    public List<String> doReturn(List<String> dataList) {
        if (dataList == null) {
            return Collections.emptyList();
        }
        return dataList;
    }
}
