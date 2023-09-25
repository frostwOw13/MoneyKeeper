import React from "react";
import {Form} from "react-router-dom";
import Modal from 'react-modal';

// library
import {UserIcon, UserPlusIcon} from "@heroicons/react/24/solid";

// assets
import illustration from "../assets/illustration.jpg"

const customStyles = {
    content: {
        top: '50%',
        left: '50%',
        right: 'auto',
        bottom: 'auto',
        marginRight: '-50%',
        transform: 'translate(-50%, -50%)',
    },
};

const Intro = () => {
    const [modalIsOpen, setIsOpen] = React.useState(false);

    const openModal = (e) => {
        e.preventDefault();
        setIsOpen(true);
    }

    const closeModal = () => {
        setIsOpen(false);
    }

    return (
        <div className="intro">
            <div>
                <h1>
                    Take Control of <span className="accent">Your Money</span>
                </h1>
                <p>
                    Personal budgeting is the secret to financial freedom. Start your journey today.
                </p>
                <Form method="post">
                    <input
                        type="text"
                        name="username"
                        required
                        placeholder="What is your name?" aria-label="Your Name" autoComplete="given-name"
                    />
                    <input
                        type="password"
                        name="password"
                        required
                        placeholder="What is your password?" aria-label="Your Password"/>

                    <div className="btn-container">
                        <input type="hidden" name="_action" value="signin"/>
                        <button type="submit" className="btn btn--dark">
                            <span>Sign in</span>
                            <UserIcon width={20}/>
                        </button>

                        <button className="btn btn--white" onClick={openModal}>
                            <span>Sign up</span>
                            <UserPlusIcon width={20}/>
                        </button>
                    </div>
                </Form>

                <Modal
                    isOpen={modalIsOpen}
                    onRequestClose={closeModal}
                    style={customStyles}
                    ariaHideApp={false}
                    contentLabel="Sign up"
                >
                    <h2 className="modal-header">Sign up to MoneyKeeper</h2>
                    <Form method="post" className="modal-form">
                        <input
                            type="text"
                            name="username"
                            required
                            placeholder="What is your name?" aria-label="Your Name" autoComplete="given-name"
                        />
                        <input
                            type="email"
                            name="email"
                            required
                            placeholder="What is your email?" aria-label="Your Email"
                        />
                        <input
                            type="password"
                            name="password"
                            required
                            placeholder="What is your password?" aria-label="Your Password"/>

                        <input type="hidden" name="_action" value="signup"/>
                        <button className="btn btn--white">
                            <span>Sign up</span>
                            <UserPlusIcon width={20}/>
                        </button>
                    </Form>
                </Modal>
            </div>
            <img src={illustration} alt="Person with money" width={600}/>
        </div>
    )
}
export default Intro