package com.kms.demo.db;// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models

public class CountryEntity {

    /**
     * 英文名称简写
     */
    private String countryShortEnName;
    /**
     * 国家代码
     */
    private Long countryCode;
    /**
     * 英文名称
     */
    private String countryEnName;
    /**
     * 中文名称
     */
    private String countryZhName;

    public CountryEntity(String countryShortEnName, Long countryCode, String countryEnName, String countryZhName) {
        this.countryShortEnName = countryShortEnName;
        this.countryCode = countryCode;
        this.countryEnName = countryEnName;
        this.countryZhName = countryZhName;
    }

    public String getCountryShortEnName() {
        return countryShortEnName;
    }

    public void setCountryShortEnName(String countryShortEnName) {
        this.countryShortEnName = countryShortEnName;
    }

    public Long getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Long countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryEnName() {
        return countryEnName;
    }

    public void setCountryEnName(String countryEnName) {
        this.countryEnName = countryEnName;
    }

    public String getCountryZhName() {
        return countryZhName;
    }

    public void setCountryZhName(String countryZhName) {
        this.countryZhName = countryZhName;
    }


    @Override
    public String toString() {
        return "Country{" +
                "countryShortEnName='" + countryShortEnName + '\'' +
                ", countryCode=" + countryCode +
                ", countryEnName='" + countryEnName + '\'' +
                ", countryZhName='" + countryZhName + '\'' +
                '}';
    }
}
