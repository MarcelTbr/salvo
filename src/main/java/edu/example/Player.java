package edu.example;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


    /**
     * Tells Spring to create the table Players
     * */
        @Entity
        public class Player {

            @Id
            @GeneratedValue(strategy=GenerationType.AUTO)
            /** long extends the number limit of int */
            private long id;
            private String username;
            private String email;


            /**
             * JAVA Class default constructor, for SpringBoot to use
             * necessary because we declared a user constructor below
             * */
            public Player() {}

            /**
             * JAVA Class user constructor
             * */
            public Player (String usr, String eml){

                /**
                 * "this."property is assumed
                 * */

                username = usr;
                email = eml;

            }

            public String getUsername() {
                return username;
            }

            public void setUsername (String usr) {
                this.username = usr;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String eml) {
                this.email = eml;
            }

            /**
             * Tell Java Library to use this method instead of the standard one
             * */
            @Override
            public String toString() {
                return username + " " + email;
            }


        }