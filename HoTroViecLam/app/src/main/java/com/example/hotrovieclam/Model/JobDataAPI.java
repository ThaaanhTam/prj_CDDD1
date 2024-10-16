package com.example.hotrovieclam.Job;

import java.util.List;

public class JobDataAPI {

    private List<Job> results;

    public static class Job {
        private int id;
        private String extId;
        private Company company;
        private String title;
        private String location;
        private List<Type> types;
        private List<City> cities;
        private List<Country> countries;
        private boolean hasRemote;
        private String published;

        public String getExtId() {
            return extId;
        }

        public void setExtId(String extId) {
            this.extId = extId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setCompany(Company company) {
            this.company = company;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public List<City> getCities() {
            return cities;
        }

        public void setCities(List<City> cities) {
            this.cities = cities;
        }

        public List<Country> getCountries() {
            return countries;
        }

        public void setCountries(List<Country> countries) {
            this.countries = countries;
        }

        public boolean isHasRemote() {
            return hasRemote;
        }

        public void setHasRemote(boolean hasRemote) {
            this.hasRemote = hasRemote;
        }

        public String getPublished() {
            return published;
        }

        public void setPublished(String published) {
            this.published = published;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        private String description;

        public List<Type> getTypes() {
            return types;
        }

        public void setTypes(List<Type> types) {
            this.types = types;
        }


        public static class Company {
            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getGithubUrl() {
                return githubUrl;
            }

            public void setGithubUrl(String githubUrl) {
                this.githubUrl = githubUrl;
            }

            public String getTwitterHandle() {
                return twitterHandle;
            }

            public void setTwitterHandle(String twitterHandle) {
                this.twitterHandle = twitterHandle;
            }

            public String getLinkedinUrl() {
                return linkedinUrl;
            }

            public void setLinkedinUrl(String linkedinUrl) {
                this.linkedinUrl = linkedinUrl;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public String getWebsiteUrl() {
                return websiteUrl;
            }

            public void setWebsiteUrl(String websiteUrl) {
                this.websiteUrl = websiteUrl;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            private int id;
            private String name;
            private String logo;  // Đây là URL của logo
            private String websiteUrl;
            private String linkedinUrl;
            private String twitterHandle;
            private String githubUrl;

            // Getters
            public String getLogo() {
                return logo;
            }
        }

        // Class mô tả loại công việc
        public static class Type {
            private int id;
            private String name;
        }

        // Class mô tả thành phố
        public static class City {
            private int geonameid;
            private String name;
            private Country country;

            // Class mô tả quốc gia
            public static class Country {
                private String code;
                private String name;
                private Region region;

                // Class mô tả khu vực
                public static class Region {
                    private int id;
                    private String name;
                }
            }
        }

        // Class mô tả quốc gia
        public static class Country {
            private String code;
            private String name;
            private Region region;

            // Class mô tả khu vực
            public static class Region {
                private int id;
                private String name;
            }
        }

        // Getters
        public Company getCompany() {
            return company;
        }

    }

    // Getters
    public List<Job> getResults() {
        return results;
    }

    // Setters
    public void setResults(List<Job> results) {
        this.results = results;
    }
}
