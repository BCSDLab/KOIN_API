package koreatech.in.controller;

import koreatech.in.domain.Criteria.SearchCriteria;
import koreatech.in.service.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

@Controller
public class SearchController {
    @Inject
    private SearchService searchService;

    @RequestMapping(value = "/articles/search", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity searchCommunity(@ModelAttribute("searchCriteria") SearchCriteria searchCriteria) throws Exception {

        return new ResponseEntity<>(searchService.searchCommunity(searchCriteria), HttpStatus.OK);
    }

    @RequestMapping(value = "/shops/search", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity searchShop(@ModelAttribute("searchCriteria") SearchCriteria searchCriteria) throws Exception {

        return new ResponseEntity<>(searchService.searchShop(searchCriteria), HttpStatus.OK);
    }
}
