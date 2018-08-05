import React, { Component } from 'react';

export class Transactions extends Component {

    constructor(props) {
        super(props);
        this.state = { weeks: [], loading: true };
        
        var headers = new Headers();
        headers.append('Accept', 'application/json');
        headers.append('Authorization', 'Bearer ' + localStorage.getItem('access_token'));
        
        var postRequest = new Request('http://localhost:8080/fetchTransactions', {
            method: "POST",
            headers: headers
        });

        fetch(postRequest)
            .then(response => response.json())
            .then();
        
        var getRequest = new Request('http://localhost:8080/api', {
            headers: headers
        });

        fetch(getRequest)
            .then(response => response.json())
            .then(data => {
                this.setState({ weeks: data, loading: false });
            });
    }

    static renderTransactions(weeks) {
        return (
            <div>
                {weeks.map(week => 
                    <div>
                    <h1 key={week.id}>Week starting {week.startDate.toString()}</h1>
                 
                    {week.transactions.map(tran =>
                        <p key={tran.id}>
                        {tran.amount} on {tran.date} at {tran.location}
                        </p>
                    )}
                    </div>
                )}
                
            </div>
        )
    }

    render() {
        let contents = this.state.loading
            ? <p><em>Loading...</em></p>
            : Transactions.renderTransactions(this.state.weeks);

        return (
            <div>
                <h1>Lunch by week</h1>
                {contents}
            </div>
        );
    }
}
