
package com.example.demo;

import java.io.Serializable;

public class FmsData implements Serializable {
    private String type;
    private String termNo;
    private String drawDate;
    private String drawNumber;
    private String sum;
    private String dragonTiger;

    /**
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return termNo
     */
    public String getTermNo() {
        return termNo;
    }

    /**
     * @param termNo termNo
     */
    public void setTermNo(String termNo) {
        this.termNo = termNo;
    }

    /**
     * @return drawDate
     */
    public String getDrawDate() {
        return drawDate;
    }

    /**
     * @param drawDate drawDate
     */
    public void setDrawDate(String drawDate) {
        this.drawDate = drawDate;
    }

    /**
     * @return drawNumber
     */
    public String getDrawNumber() {
        return drawNumber;
    }

    /**
     * @param drawNumber drawNumber
     */
    public void setDrawNumber(String drawNumber) {
        this.drawNumber = drawNumber;
    }

    /**
     * @return sum
     */
    public String getSum() {
        return sum;
    }

    /**
     * @param sum sum
     */
    public void setSum(String sum) {
        this.sum = sum;
    }

    /**
     * @return dragonTiger
     */
    public String getDragonTiger() {
        return dragonTiger;
    }

    /**
     * @param dragonTiger dragonTiger
     */
    public void setDragonTiger(String dragonTiger) {
        this.dragonTiger = dragonTiger;
    }
}
