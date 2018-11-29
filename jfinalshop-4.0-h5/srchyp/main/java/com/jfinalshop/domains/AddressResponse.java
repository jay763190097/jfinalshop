package com.jfinalshop.domains;

import com.jfinalshop.model.Receiver;

import java.util.List;

public class AddressResponse extends DatumResponse{

    private List<Receiver> data;

    public List<Receiver> getData() {
        return data;
    }

    public void setData(List<Receiver> data) {
        this.data = data;
    }
}
