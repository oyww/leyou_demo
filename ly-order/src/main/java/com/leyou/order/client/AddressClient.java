package com.leyou.order.client;

import com.leyou.order.dto.AddressDto;

import java.util.ArrayList;
import java.util.List;

public abstract class AddressClient {
    public static final List<AddressDto> addressList = new ArrayList<AddressDto>(){
        {
            AddressDto address = new AddressDto();
            address.setId(1L);
            address.setAddress("太白南路");
            address.setCity("西安");
            address.setDistrict("雁塔区");
            address.setName("max");
            address.setPhone("15656789988");
            address.setState("陕西");
            address.setZipCode("7100710");
            address.setIsDefault(true);
            add(address);

            AddressDto address2 = new AddressDto();
            address2.setId(2L);
            address2.setAddress("学院路三号");
            address2.setCity("太原");
            address2.setDistrict("尖草坪区");
            address2.setName("su");
            address2.setPhone("15656783344");
            address2.setState("山西");
            address2.setZipCode("03500150");
            address2.setIsDefault(false);
            add(address2);
        }
    };

    public static AddressDto findById(Long id){
        for (AddressDto addressDTO : addressList) {
            if(addressDTO.getId() == id){
                return addressDTO;
            }
        }
        return null;
    }

}
