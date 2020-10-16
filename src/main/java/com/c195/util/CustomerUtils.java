package com.c195.util;

import com.c195.common.AddressDTO;

public final class CustomerUtils {

    public static String toAddressLine(AddressDTO addressDTO) {
        return String.format("%s %s %s, %s, %s",
                addressDTO.getAddress(),
                addressDTO.getAddress2(),
                addressDTO.getCity(),
                addressDTO.getCountry(),
                addressDTO.getPostalCode());
    }

    public static String toActiveLine(boolean activeStatus) {
        return activeStatus ? "Active" : "Inactive";
    }
}
