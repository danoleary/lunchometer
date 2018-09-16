import React from 'react';
import ReactDOM from 'react-dom';
import { Week } from './week';
import { NavBar } from './navBar';

class Home extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            lunchWeeks: [],
            showTransactions: []
        };
        this.handleClick = this.handleClick.bind(this);
    }

    chunkArray(myArray, chunk_size){
        var results = [];
        
        while (myArray.length) {
            results.push(myArray.splice(0, chunk_size));
        }
        
        return results;
    }

    componentDidMount() {
        fetch("http://localhost:8080/transactions/1234")
          .then(res => res.json())
          .then(
            (result) => {
              this.setState({
                lunchWeeks: result,
                showTransactions: result.map(x => ({
                    id: x.id,
                    showDetail: false
                }))
              });
            },
            (error) => {
              this.setState({
                error
              });
            }
          )
      }

    handleClick(id) {
        const currentState = this.state
            .showTransactions
            .filter(x => x.id === id)[0]
            .showDetail;
        this.setState({
            lunchWeeks: this.state.lunchWeeks,
            showTransactions: this.state
                .showTransactions
                .filter(x => x.id !== id)
                .concat([{id: id, showDetail: !currentState}])
        })
    }

    render() {
        return (
            <div>
                <NavBar />
                <section class="section">
                    <div class="container">
                        <div class="columns is-multiline is-mobile">
                        {this.state.lunchWeeks.map(week => (
                            <Week
                                week={week}
                                showTransactions={this.state.showTransactions}
                                key={week.id}
                                onClick={this.handleClick} />
                        ))}
                        </div>
                    </div>
                </section>
            </div>
        );
    }
}

ReactDOM.render(<Home />, document.getElementById('app'));