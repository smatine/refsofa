package com.example.referentiel.controller;

import com.example.referentiel.exception.ResourceNotFoundException;
import com.example.referentiel.model.Account;
import com.example.referentiel.model.Product;
import com.example.referentiel.model.Region;
import com.example.referentiel.repository.AccountRepository;
import com.example.referentiel.repository.ProductRepository;
import com.example.referentiel.repository.RegionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
//@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;


    @GetMapping("/products/{productId}/accounts")
    public List<Account> getAccountsByProductId(@PathVariable Long productId) {
        return accountRepository.findByProductId(productId);
    }
    
    
    @GetMapping("/accounts")
    Collection<Account> accounts() {
    	
        return accountRepository.findAll();
    }

    @GetMapping("/accounts/{id}")
    ResponseEntity<?> getAccount(@PathVariable Long id) {
        Optional<Account> account = accountRepository.findById(id);
        
        System.out.println("account:" +  account.toString());
        
        return account.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    /*
    @GetMapping("/account/{id}")
    ResponseEntity<?> getAccoun(@PathVariable String id) {
    	
    	Long idl = Long.valueOf(id);
        Optional<Account> account = accountRepository.findById(idl);
        
        System.out.println("account:" +  account.toString());
        
        return account.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    */
    /*
    @PostMapping("/trigrammes/{trigrammeId}/accounts")
    public Account addAccount(@PathVariable Long trigrammeId,
                            @Valid @RequestBody Account account) {
        return trigrammeRepository.findById(trigrammeId)
                .map(trigramme -> {
                    account.setTrigramme(trigramme);
                    return accountRepository.save(account);
                }).orElseThrow(() -> new ResourceNotFoundException("Trigramme not found with id " + trigrammeId));
    }
    */
    @PostMapping("/products/{productId}/accounts")
    public Account addAccount(@PathVariable String productId,
                            @Valid @RequestBody Account account) {
    	
    	long triId = Long.valueOf(productId);
        return productRepository.findById(triId)
                .map(product -> {
                    account.setProduct(product);
                    return accountRepository.save(account);
                }).orElseThrow(() -> new ResourceNotFoundException("product not found with id " + productId));
    }
    
    @PutMapping("/products/{productId}/accounts/{accountId}")
    public Account updateAccount(@PathVariable Long productId,
                               @PathVariable Long accountId,
                               @Valid @RequestBody Account accountRequest) {
        if(!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("product not found with id " + productId);
        }
        Optional<Product> product = productRepository.findById(productId);
        
        //Optional<Region> region = regionRepository.findById(accountRequest.getRegion().getId());
        
        return accountRepository.findById(accountId)
                .map(account -> {
                    account.setText(accountRequest.getText());
                    account.setEnv(accountRequest.getEnv());
                    account.setNumAccount(accountRequest.getNumAccount());
                    account.setMailList(accountRequest.getMailList());
                    //account.setRegion(region.get());
                    account.setAlias(accountRequest.getAlias());
                    account.setProduct(product.get());
                    return accountRepository.save(account);
                }).orElseThrow(() -> new ResourceNotFoundException("Account not found with id " + accountId));
    }

    @DeleteMapping("/products/{productId}/accounts/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long productId,
                                          @PathVariable Long accountId) {
        if(!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("product not found with id " + productId);
        }

        return accountRepository.findById(accountId)
                .map(account -> {
                	/*Region region = account.getRegion();
                	region.getAccounts().remove(account);
                	regionRepository.save(region);
                	*/
                    accountRepository.delete(account);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Account not found with id " + accountId));

    }
    /*
    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity<?> deleteAccount(
                                          @PathVariable Long accountId) {
        
        return accountRepository.findById(accountId)
                .map(account -> {
                    accountRepository.delete(account);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Account not found with id " + accountId));

    }*/
}
