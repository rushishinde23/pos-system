package com.zosh.modal;


import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class BranchTest {


    @Test
    void testBranch(){


        Branch branch =
                Branch.builder()
                        .id(1L)
                        .name("Pune Branch")
                        .address("Pune")
                        .workingDays(
                                List.of("MON","TUE"))
                        .build();



        assertEquals(
                "Pune Branch",
                branch.getName());


        assertEquals(
                2,
                branch.getWorkingDays().size());


    }


    @Test
    void testBranchDates(){

        Branch branch=new Branch();


        branch.onCreate();

        assertNotNull(branch.getCreatedAt());


        branch.onUpdate();

        assertNotNull(branch.getUpdatedAt());


    }

}