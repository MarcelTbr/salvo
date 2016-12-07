package edu.example

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

        @Entity
        public class Player {

            @Id
            @GeneratedValue(strategy=GenerationType.AUTO)
            private long id;
            private String username;
            private String email;


            public Player (String usr, String eml{

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

            public String toString() {
                return username + " " + email;
            }


        }