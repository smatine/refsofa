import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom';
import { Button, Container, Form, FormGroup, Input, Label,FormFeedback  } from 'reactstrap';
import AppNavbar from './AppNavbar';
import { API_BASE_URL, FRT_BASE_URL } from './constants';

class AccountEdit extends Component {

  emptyItem = {
    numAccount: '',
    text: '',
	  env: '',
    mailList: '',
    alias: '',
    products: {},
	  product: {},
	  productId : '',
    touched: {
      vpc:false,
      numAccount: false,
      env: false,
      mailList: false,
      alias: false,
      productId: false
    }
  };

  constructor(props) {
    super(props);
    this.state = {
      item: this.emptyItem
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleBlur = this.handleBlur.bind(this);
  }

  async componentDidMount() {
    if (this.props.match.params.id !== 'new') {
      const account = await (await fetch(`${API_BASE_URL}/accounts/${this.props.match.params.id}`)).json();
      account.touched = {
        vpc: false,
        numAccount: false,
        env: false,
        mailList: false,
        alias: false,
        productId: false
      };
      this.setState({item: account});

      let item = {...this.state.item};
      item.productId = account.product.id;
      this.setState({item: item});

      let vpcs;                              
      await fetch(`${API_BASE_URL}/accounts/${this.props.match.params.id}/vpcs`)
      .then((result) => {
        return result.json();
      }).then((jsonResult) => {
        vpcs = jsonResult.length;
        console.log(jsonResult.length);
      })
      if(vpcs !== 0) {
        let item = {...this.state.item};
        item.touched.vpc = true;
      }

    }
    else {
      const account = {
        numAccount: '',
        text: '',
        env: '',
        mailList: '',
        alias: '',
        products: {},
        product: {},
        productId : '',
        touched: {
          vpc: false,
          numAccount: false,
          env: false,
          mailList: false,
          alias: false,
          productId: false
        }
      };
      account.touched = {
        vpc: false,
        numAccount: false,
        env: false,
        mailList: false,
        alias: false,
        productId: false
      };
      this.setState({item: account});
    }

    //const {item} = this.state;
    await fetch(API_BASE_URL + '/products',)
    .then((result) => {
      return result.json();
    }).then((jsonResult) => {
      let item = {...this.state.item};
      item.products = jsonResult;
      this.setState({item: item});
    })

  }

  handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    let item = {...this.state.item};
    item[name] = value;
    this.setState({item});
  }

  async handleSubmit(event) {
    event.preventDefault();
    const {item} = this.state;
    
    item.touched = {
        numAccount: true,
        env: true,
        mailList: true,
        alias: true,
        productId: true
    };
    const errors = this.validate(item.numAccount, item.env, item.mailList, item.productId, item.alias);
    const isDisabled = Object.keys(errors).some(x => errors[x]);
    if(isDisabled) {
        this.forceUpdate();
        return;
    }
    
    const hist= FRT_BASE_URL + '/accounts'; 

    item.product={id: item.productId};

    await fetch((item.id) ? API_BASE_URL + '/products/' + (item.product.id) + '/accounts/'+(item.id) : API_BASE_URL + '/products/' + item.product.id + '/accounts', {
      method: (item.id) ? 'PUT' : 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(item),
    });
    this.props.history.push(hist);
  }

  handleBlur = (field) => (evt) => {

    let item = {...this.state.item};
    item.touched= { ...this.state.item.touched, [field]: true};
    this.setState({item});

  }
  

  validate(numAccount, env, mailList, productId, alias) {

    const errors = {
      numAccount: '',
      env: '',
      mailList: '',
      alias: '',
      productId: ''
    };
    
    if(this.state.item.touched.numAccount && numAccount.length !== 12){
      errors.numAccount = 'Account should not be null';
      return errors;
    }
    else if(this.state.item.touched.productId && productId.length <= 0){
      errors.productId = 'Product should not be null';
      return errors;
    }
    else if(this.state.item.touched.env && env.length !== 3){
      errors.env = 'Env should be = 3 characters!';
      return errors;
    }
    else if(this.state.item.touched.alias && alias.length <= 0){
      errors.alias = 'alias should not be null';
      return errors;
    }

    const emailRex = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    
    if(this.state.item.touched.mailList && !emailRex.test(mailList)){
      errors.mailList = 'Mailling List should have correct format ';
      return errors;
    }
   
    return errors;
  }

  render() {
    const {item} = this.state;
    const title = <h2>{item.id ? 'Edit Account' : 'Add Account'}</h2>;
    
    const errors = this.validate(item.numAccount, item.env, item.mailList, item.productId, item.alias);
    const isDisabled = Object.keys(errors).some(x => errors[x]);
    
    const canc = FRT_BASE_URL + "/accounts";

    /*let trig = null;
    trig = <FormGroup>
            <Label for="tri">Product: {item.product.name}</Label>
            <Input type="text" name="productId" id="productId" value={item.product.id || ''} disabled="true"/>
          </FormGroup>;*/
    

    /*let vpcs = null;
    if(item.id) vpcs = <Button size="sm" color="secondary" tag={Link} to={"/account/" + item.id + "/vpcs"}>Vpcs</Button>;*/

    const isDisabledd = item.touched.vpc;

    
    let opts = [];
    if(item.products && item.products.length){
          item.products.map(s => {  
          opts.push(<option key={s.id}  value={s.id}>{s.id} {s.name}</option>);
      });
    }
    if(item.id) {
          //opts.push(<option value={item.product.id} >{item.product.id} {item.product.name} </option>);
    }

    let prd = item.productId || '';
    item.productId = prd;


    return <div>
      <AppNavbar/>
      <Container>
        {title}
        <Form onSubmit={this.handleSubmit}>
          <FormGroup>
            <Label for="numAccount">Account (*)</Label>
            <Input type="text" name="numAccount" id="numAccount" value={item.numAccount || ''} placeholder="Enter account"  maxLength="12"
                   onChange={this.handleChange} onBlur={this.handleBlur('numAccount')} autoComplete="numAccount"
                   valid={errors.numAccount === ''}
                   invalid={errors.numAccount !== ''}
            />
            <FormFeedback>{errors.numAccount}</FormFeedback>
          </FormGroup>
          
          <FormGroup>
            <Label for="productId">Product (*)</Label>
            <Input type="select" name="productId" id="productId"  value={prd} onChange={this.handleChange} onBlur={this.handleBlur('productId')}
                 valid={errors.productId === ''}
                 invalid={errors.productId !== ''}
            >
              <option value="" disabled>Choose</option>
              {opts}
            </Input>
            <FormFeedback>{errors.productId}</FormFeedback>
          </FormGroup>


          <FormGroup>
            <Label for="env">Env (*)</Label>
            <Input type="select" name="env" id="env" value={item.env || ''} onChange={this.handleChange} onBlur={this.handleBlur('env')} 
                 valid={errors.env === ''}
                 invalid={errors.env !== ''}
                 disabled={isDisabledd}
            >
              <option value="" disabled>Choose</option>
              <option value="DEV">DEV</option>
              <option value="HML">HML</option>
              <option value="PRD">PRD</option>
              <option value="SDB">SDB</option>
            </Input>
            <FormFeedback>{errors.env}</FormFeedback>
          </FormGroup>

          <FormGroup>
            <Label for="mailList">Mail (*)</Label>
            <Input  type="text" name="mailList" id="mailList" value={item.mailList || ''} placeholder="Enter email"
                   onChange={this.handleChange} onBlur={this.handleBlur('mailList')} autoComplete="mailList"
                   valid={errors.mailList === ''}
                   invalid={errors.mailList !== ''}
                   />
            <FormFeedback>{errors.mailList}</FormFeedback>
          </FormGroup>
		  
          <FormGroup>
            <Label for="alias">Alias (*)</Label>
            <Input  type="text" name="alias" id="alias" value={item.alias || ''} placeholder="Enter alias"
                   onChange={this.handleChange} onBlur={this.handleBlur('alias')} autoComplete="alias"
                   valid={errors.alias === ''}
                   invalid={errors.alias !== ''}
                   />
            <FormFeedback>{errors.alias}</FormFeedback>
          </FormGroup>
          
		     
		      <FormGroup>
            <Label for="text">Description</Label>
            <Input type="text" name="text" id="text" value={item.text || ''}
                   onChange={this.handleChange} autoComplete="text"/>
          </FormGroup>
		  
          <FormGroup>
            <Button color="primary" type="submit" disabled={isDisabled}>Save</Button>{' '}
            <Button color="secondary" tag={Link} to={canc}>Cancel</Button>
          </FormGroup>
        </Form>
      </Container>
    </div>
  }
}

export default withRouter(AccountEdit);