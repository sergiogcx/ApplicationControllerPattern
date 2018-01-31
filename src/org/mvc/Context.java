package org.mvc;


public class Context {

    public static String api_endpoint = "http://feeds.reuters.com/reuters/";
    
    private static String[] context_options = {
            "BusinessNews", "CompanyNews", "Entertainment", "Environment",
            "HealthNews", "Lifestyle", "MostRead", "OddlyEnoughNews",
            "PeopleNews", "PoliticsNews", "ScienceNews", "SportsNews",
            "TechnologyNews", "TopNews", "DomesticNews", "WorldNews"
    };

    // Access to contexts list
    public String[] get_context_list() {
        return context_options;
    }

    // Builds RSS Feed URL
    public String get_news_url(String context) {
        return api_endpoint + context;
    }
}
